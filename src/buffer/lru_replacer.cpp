//===----------------------------------------------------------------------===//
//
//                         BusTub
//
// lru_replacer.cpp
//
// Identification: src/buffer/lru_replacer.cpp
//
// Copyright (c) 2015-2019, Carnegie Mellon University Database Group
//
//===----------------------------------------------------------------------===//

#include "buffer/lru_replacer.h"

namespace bustub {

LRUReplacer::LRUReplacer(size_t num_pages) {}

LRUReplacer::~LRUReplacer() = default;

/*
Remove the object that was accessed least recently compared to all the other elements
being tracked by the Replacer,store its contents in the output parameter and return True.
If the Replacer is empty return False.

delete frame_id & pass value.
*/
auto LRUReplacer::Victim(frame_id_t *frame_id) -> bool {
  std::lock_guard<std::mutex> guard(lru_latch_);
  if (lru_list_.empty()) {
    return false;
  }
  *frame_id = lru_list_.front();  // Only used to pass value by parameter.
  lru_map_iter_.erase(*frame_id);
  lru_list_.pop_front();
  return true;
}

/*
This method should be called after a page is pinned to a frame in the BufferPoolManager.
It should remove the frame containing the pinned page from the LRUReplacer.

Once a frame is pinned, LRURplacer should move it out (delete).
*/
void LRUReplacer::Pin(frame_id_t frame_id) {
  // frame 被 pin 了，从 Replacer 里面踢掉
  std::lock_guard<std::mutex> guard(lru_latch_);
  auto iter = lru_map_iter_.find(frame_id);
  if (iter != lru_map_iter_.end()) {
    lru_list_.erase(iter->second);  // list `erase` can only invoke by position.
    lru_map_iter_.erase(iter);
  }
}

/*
This method should be called when the pin_count of a page becomes 0.
This method should add the frame containing the unpinned page to the LRUReplacer.

Once a frame in unpinned, it is possible to move this frame out of main memory.
Hence we should add it to the back of LRUReplacer (most recently used)
*/
void LRUReplacer::Unpin(frame_id_t frame_id) {
  // frame 被 unpin 了，加到 Replacer 里面
  std::lock_guard<std::mutex> guard(lru_latch_);
  if (lru_map_iter_.find(frame_id) == lru_map_iter_.end()) {
    lru_list_.emplace_back(frame_id);
    auto iter = lru_list_.end();
    lru_map_iter_[frame_id] = --iter;
  }
}

/*
This method returns the number of frames that are currently in the LRUReplacer.

return lru_list_.size()
*/
auto LRUReplacer::Size() -> size_t {
  std::lock_guard<std::mutex> guard(lru_latch_);
  return lru_list_.size();
}
}  // namespace bustub
