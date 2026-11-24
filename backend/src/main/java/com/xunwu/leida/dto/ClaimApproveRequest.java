package com.xunwu.leida.dto;

import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/**
 * 认领通过请求体,可选 { handTime, place };两个都传或都不传,只传一个由 service 判 400
 */
public record ClaimApproveRequest(
        OffsetDateTime handTime,

        @Size(max = 120, message = "交接地点不能超过 120 字")
        String place) {
}
