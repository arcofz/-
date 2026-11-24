package com.xunwu.leida.dto;

/**
 * GET /admin/stats 返回结构
 */
public record StatsVO(long userCount, long itemCount, long lostCount, long foundCount,
                      long pendingItemCount, long completedItemCount,
                      long matchCount, long claimCount, long pendingClaimCount) {
}
