package com.xunwu.leida.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

/**
 * 物品详情里的 myClaim { id, status, createdAt },未认领时为 null
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record MyClaimVO(Long id, String status, OffsetDateTime createdAt) {
}
