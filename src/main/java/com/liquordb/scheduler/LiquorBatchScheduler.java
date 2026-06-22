package com.liquordb.scheduler;

// Disabled because liquor likes are updated immediately via LiquorLikeEvent and LikeEventListener.
// Keeping both caused double updates and negative/incorrect like counts.
public class LiquorBatchScheduler {
}
