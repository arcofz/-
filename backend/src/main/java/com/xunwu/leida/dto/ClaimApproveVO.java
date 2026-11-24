package com.xunwu.leida.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * approve 返回 { claim, handover };未登记交接时 handover 为 null(契约第 3 节)
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record ClaimApproveVO(ClaimVO claim, HandoverVO handover) {
}
