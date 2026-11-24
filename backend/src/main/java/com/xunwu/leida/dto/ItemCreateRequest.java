package com.xunwu.leida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/**
 * 发布物品请求体 { type, title, description, categoryId, locationId, eventTime, imageUrl }
 */
public record ItemCreateRequest(
        @NotBlank(message = "type 不能为空")
        @Pattern(regexp = "lost|found", message = "type 只允许 lost 或 found")
        String type,

        @NotBlank(message = "标题不能为空")
        @Size(max = 120, message = "标题不能超过 120 字")
        String title,

        String description,

        Long categoryId,

        Long locationId,

        @NotNull(message = "eventTime 不能为空")
        OffsetDateTime eventTime,

        @Size(max = 512, message = "imageUrl 不能超过 512 字符")
        String imageUrl) {
}
