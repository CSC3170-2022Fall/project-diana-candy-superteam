//===----------------------------------------------------------------------===//
//
//                         BusTub
//
// buffer_pool_manager_instance.cpp
//
// Identification: src/buffer/buffer_pool_manager.cpp
//
// Copyright (c) 2015-2021, Carnegie Mellon University Database Group
//
//===----------------------------------------------------------------------===//

#include "buffer/buffer_pool_manager_instance.h"

#include "common/macros.h"

namespace bustub {

BufferPoolManagerInstance::BufferPoolManagerInstance(size_t pool_size, DiskManager *disk_manager,
                                                     LogManager *log_manager)
    : BufferPoolManagerInstance(pool_size, 1, 0, disk_manager, log_manager) {}

BufferPoolManagerInstance::BufferPoolManagerInstance(size_t pool_size, uint32_t num_instances, uint32_t instance_index,
                                                     DiskManager *disk_manager, LogManager *log_manager)
    : pool_size_(pool_size),
      num_instances_(num_instances),
      instance_index_(instance_index),
      next_page_id_(instance_index),
      disk_manager_(disk_manager),
      log_manager_(log_manager) {
  BUSTUB_ASSERT(num_instances > 0, "If BPI is not part of a pool, then the pool size should just be 1");
  BUSTUB_ASSERT(
      instance_index < num_instances,
      "BPI index cannot be greater than the number of BPIs in the pool. In non-parallel case, index should just be 1.");
  // We allocate a consecutive memory space for the buffer pool.
  pages_ = new Page[pool_size_];
  replacer_ = new LRUReplacer(pool_size);

  // Initially, every page is in the free list.
  for (size_t i = 0; i < pool_size_; ++i) {
    free_list_.emplace_back(static_cast<int>(i));
  }
}

BufferPoolManagerInstance::~BufferPoolManagerInstance() {
  delete[] pages_;
  delete replacer_;
}

auto BufferPoolManagerInstance::FlushPgImp(page_id_t page_id) -> bool {
  // Make sure you call DiskManager::WritePage!
  // Input: page_id -> frame_id (in the buffer pool)
  // 根据 page_id 把 buffer pool 里的 frame_id flush 掉.
  std::lock_guard<std::mutex> guard(latch_);
  auto frame_id_iter = page_table_.find(page_id);
  if (frame_id_iter == page_table_.end()) {  // frame not found
    return false;
  }
  Page *page = pages_ + frame_id_iter->second;
  page->is_dirty_ = false;
  disk_manager_->WritePage(page_id, page->GetData());
  return true;
}

void BufferPoolManagerInstance::FlushAllPgsImp() {
  // You can do it!
  // 直接把 page_table_ 全拉出来 flush
  std::lock_guard<std::mutex> guard(latch_);
  for (auto &[page_id, frame_id] : page_table_) {
    Page *page = pages_ + frame_id;
    page->is_dirty_ = false;
    disk_manager_->WritePage(page_id, page->GetData());
  }
  // page_table_.clear(); Only flush pages to disk, not remove them from page_table_ !!!
}

auto BufferPoolManagerInstance::NewPgImp(page_id_t *page_id) -> Page * {
  // 0.   Make sure you call AllocatePage!
  // 1.   If all the pages in the buffer pool are pinned, return nullptr.
  // 2.   Pick a victim page P from either the free list or the replacer. Always pick from the free list first.
  // 3.   Update P's metadata, zero out memory and add P to the page table.
  // 4.   Set the page ID output parameter. Return a pointer to P.
  std::lock_guard<std::mutex> guard(latch_);
  Page *page = nullptr;
  frame_id_t frame_id;
  if (!free_list_.empty()) {  // free list 能用直接拿来用. (buffer pool 里干净的)
    frame_id = free_list_.front();
    free_list_.pop_front();
    page = pages_ + frame_id;
  } else if (replacer_->Victim(
                 &frame_id)) {  // free list has no place, but replacer has a victim, 直接踢掉用作 new page.
    page = pages_ + frame_id;
    if (page->is_dirty_) {  // 脏了写回去.
      disk_manager_->WritePage(page->GetPageId(), page->GetData());
    }
    page_table_.erase(page->GetPageId());  // 旧的 page 从 table 里删掉
  }
  // return 一个干净的 page*
  if (page != nullptr) {
    *page_id = AllocatePage();
    page->page_id_ = *page_id;
    page->ResetMemory();
    page->pin_count_ = 1;
    page->is_dirty_ = false;
    page_table_[*page_id] = frame_id;
    // 申请 newpage 先 pin 住
    replacer_->Pin(frame_id);
    return page;
  }
  // no page in free list & all pages are pinned.
  return nullptr;
}

auto BufferPoolManagerInstance::FetchPgImp(page_id_t page_id) -> Page * {
  // 1.     Search the page table for the requested page (P).
  // 1.1    If P exists, pin it and return it immediately.
  // 1.2    If P does not exist, find a replacement page (R) from either the free list or the replacer.
  //        Note that pages are always found from the free list first.
  // 2.     If R is dirty, write it back to the disk.
  // 3.     Delete R from the page table and insert P.
  // 4.     Update P's metadata, read in the page content from disk, and then return a pointer to P.
  std::lock_guard<std::mutex> guard(latch_);
  for (auto &[table_page_id, table_frame_id] : page_table_) {
    if (page_id == table_page_id) {
      // 要 fetch page 先 pin 住
      replacer_->Pin(table_frame_id);
      Page *page = pages_ + table_frame_id;
      page->pin_count_++;
      return page;
    }
  }

  // fetch 不到指定 page_id 咋办？ 从 free list 或者 replacer 里找一个 victim
  // 要指定为参数里传的 page_id !!!!!

  // Page *page = NewPgImp(&page_id);
  // if(page != nullptr) return page;

  Page *page = nullptr;
  frame_id_t frame_id = -1;
  if (!free_list_.empty()) {  // free list 能用直接拿来用. (buffer pool 里干净的)
    frame_id = free_list_.front();
    free_list_.pop_front();
    page = pages_ + frame_id;
  } else if (replacer_->Victim(
                 &frame_id)) {  // free list has no place, but replacer has a victim, 直接踢掉用作 new page.
    page = pages_ + frame_id;
    if (page->is_dirty_) {  // 脏了写回去.
      disk_manager_->WritePage(page->GetPageId(), page->GetData());
    }
    page_table_.erase(page->GetPageId());  // 旧的 page 从 table 里删掉
  }

  if (page != nullptr) {
    // 要指定 page_id 为传入的参数，不然过不了 test case.
    page->page_id_ = page_id;
    page->pin_count_ = 1;  // 从 free list or replacer 拉出来后，先 pin 住
    page->is_dirty_ = false;
    replacer_->Pin(frame_id);
    disk_manager_->ReadPage(page_id, page->GetData());
    page_table_[page_id] = frame_id;
    return page;
  }
  return nullptr;
  // return nullptr;
}

auto BufferPoolManagerInstance::DeletePgImp(page_id_t page_id) -> bool {
  // 0.   Make sure you call DeallocatePage!
  // 1.   Search the page table for the requested page (P).
  // 1.   If P does not exist, return true.
  // 2.   If P exists, but has a non-zero pin-count, return false. Someone is using the page.
  // 3.   Otherwise, P can be deleted. Remove P from the page table, reset its metadata and return it to the free list.
  std::lock_guard<std::mutex> guard(latch_);
  auto frame_id_iter = page_table_.find(page_id);
  if (frame_id_iter == page_table_.end()) {  // page not found
    return true;
  }
  frame_id_t frame_id = frame_id_iter->second;
  Page *page = pages_ + frame_id;
  if (page->pin_count_ != 0) {  // page is being used
    return false;
  }
  // page exsits (can be deleted)
  page_table_.erase(page_id);  // page is not being used, delete it
  // DeallocatePage(page_id);
  page->ResetMemory();
  page->pin_count_ = 0;
  page->is_dirty_ = false;
  page->page_id_ = INVALID_PAGE_ID;
  replacer_->Unpin(frame_id);
  free_list_.emplace_back(frame_id);
  return true;
}

auto BufferPoolManagerInstance::UnpinPgImp(page_id_t page_id, bool is_dirty) -> bool {
  std::lock_guard<std::mutex> guard(latch_);
  auto frame_id_iter = page_table_.find(page_id);
  if (frame_id_iter == page_table_.end()) {  // page not found
    return false;
  }
  // otherwise find this page
  frame_id_t frame_id = frame_id_iter->second;
  Page *page = pages_ + frame_id;
  if (page->pin_count_ == 0) {  // page is not pinned (invalid case)
    return false;
  }
  page->pin_count_--;
  if (page->pin_count_ == 0) {  // page is not pinned
    replacer_->Unpin(frame_id);
  }
  // actually I do not know the meaning of is_dirty
  if (is_dirty) {
    page->is_dirty_ = true;
  }
  return true;
}

auto BufferPoolManagerInstance::AllocatePage() -> page_id_t {
  const page_id_t next_page_id = next_page_id_;
  next_page_id_ += num_instances_;  // num_instances_ == 1
  ValidatePageId(next_page_id);
  return next_page_id;
}

void BufferPoolManagerInstance::ValidatePageId(const page_id_t page_id) const {
  assert(page_id % num_instances_ == instance_index_);  // allocated pages mod back to this BPI
}

}  // namespace bustub
