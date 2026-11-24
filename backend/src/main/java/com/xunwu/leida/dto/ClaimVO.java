package com.xunwu.leida.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

/**
 * 认领记录视图,字段严格按契约第 2 节;null 字段也要输出
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record ClaimVO(Long id, Long itemId, String itemTitle, String itemType,
                      Long claimantId, String claimantName, Long ownerId, String ownerName,
                      String evidence, String status, String reason,
                      OffsetDateTime createdAt, OffsetDateTime reviewedAt, Long handoverId) {
}
