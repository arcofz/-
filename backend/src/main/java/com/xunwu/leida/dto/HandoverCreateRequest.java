package com.xunwu.leida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/**
 * 补登记交接请求体 { claimId, handTime, place }
 */
public record HandoverCreateRequest(
        @NotNull(message = "claimId 不能为空")
        Long claimId,

        @NotNull(message = "交接时间不能为空")
        OffsetDateTime handTime,

        @NotBlank(message = "交接地点不能为空")
        @Size(max = 120, message = "交接地点不能超过 120 字")
        String place) {
}
