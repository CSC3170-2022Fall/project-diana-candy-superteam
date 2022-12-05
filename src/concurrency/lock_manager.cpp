//===----------------------------------------------------------------------===//
//
//                         BusTub
//
// lock_manager.cpp
//
// Identification: src/concurrency/lock_manager.cpp
//
// Copyright (c) 2015-2019, Carnegie Mellon University Database Group
//
//===----------------------------------------------------------------------===//

#include "concurrency/lock_manager.h"

#include <utility>
#include <vector>

namespace bustub {

/*
  * [LOCK_NOTE]: For all locking functions, we:
  * 1. return false if the transaction is aborted; and
  * 2. block on wait, return true when the lock request is granted; and
  * 3. it is undefined behavior to try locking an already locked RID in the
  * same transaction, i.e. the transaction is responsible for keeping track of
  * its current locks.
  */

/*
  * TransactionState:
  * 1. GROWING,
  * 2. SHRINKING,
  * 3. COMMITTED,
  * 4. ABORTED
  * 
  * AbortReason:
  * 1. LOCK_ON_SHRINKING,
  * 2. UNLOCK_ON_SHRINKING,
  * 3. UPGRADE_CONFLICT,
  * 4. DEADLOCK,
  * 5. LOCKSHARED_ON_READ_UNCOMMITTED
  * 
  * IsolationLevel:
  * 1. READ_UNCOMMITTED,
  * 2. READ_COMMITTED,
  * 3. REPEATABLE_READ
  */

auto LockManager::WaitForLock(Transaction *txn, LockRequestQueue *lock_queue) -> bool {
  return true;
}

auto LockManager::LockShared(Transaction *txn, const RID &rid) -> bool {
  if (txn->GetState() == TransactionState::ABORTED) return false;
  if (txn->GetIsolationLevel() == IsolationLevel::READ_UNCOMMITTED) {
    // READ_UNCOMMITTED isolation level, no need to share lock
    txn->SetState(TransactionState::ABORTED);
    throw TransactionAbortException(txn->GetTransactionId(), AbortReason::LOCKSHARED_ON_READ_UNCOMMITTED);
  }
  if (txn->GetState() != TransactionState::GROWING) {
    // 2 Phase Locking check
    txn->SetState(TransactionState::ABORTED);
    throw TransactionAbortException(txn->GetTransactionId(), AbortReason::LOCK_ON_SHRINKING);
  }
  // By default, transaction state is growing.
  // Queue for threads & use condition variable to wait.
  std::unique_lock<std::mutex> guard(latch_);


  txn->GetSharedLockSet()->emplace(rid);
  return true;
}

auto LockManager::LockExclusive(Transaction *txn, const RID &rid) -> bool {
  if (txn->GetState() == TransactionState::ABORTED) return false;
  if (txn->GetState() != TransactionState::GROWING) {
    // 2 Phase Locking check
    txn->SetState(TransactionState::ABORTED);
    throw TransactionAbortException(txn->GetTransactionId(), AbortReason::LOCK_ON_SHRINKING);
  }
  std::unique_lock<std::mutex> guard(latch_);


  txn->GetExclusiveLockSet()->emplace(rid);
  return true;
}

auto LockManager::LockUpgrade(Transaction *txn, const RID &rid) -> bool {
  if (txn->GetState() == TransactionState::ABORTED) return false;

  std::lock_guard<std::mutex> guard(latch_);


  txn->GetSharedLockSet()->erase(rid);
  txn->GetExclusiveLockSet()->emplace(rid);
  return true;
}

auto LockManager::Unlock(Transaction *txn, const RID &rid) -> bool {
  
  std::lock_guard<std::mutex> guard(latch_);

  txn->GetSharedLockSet()->erase(rid);
  txn->GetExclusiveLockSet()->erase(rid);
  return true;
}

}  // namespace bustub
