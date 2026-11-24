package com.xunwu.leida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 认领驳回请求体 { reason },契约第 5 节:必填 1–200 字
 */
public record ClaimRejectRequest(
        @NotBlank(message = "驳回理由不能为空")
        @Size(max = 200, message = "驳回理由不能超过 200 字")
        String reason) {
}
