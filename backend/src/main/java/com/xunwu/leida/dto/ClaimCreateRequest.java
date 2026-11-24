package com.xunwu.leida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 提交认领请求体 { itemId, evidence }
 */
public record ClaimCreateRequest(
        @NotNull(message = "itemId 不能为空")
        Long itemId,

        @NotBlank(message = "认领证据不能为空")
        @Size(max = 500, message = "认领证据不能超过 500 字")
        String evidence) {
}
