package com.xunwu.leida.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

/**
 * 交接详情视图,字段按契约第 6 节;联系方式给完整值(访问权限已在 service 校验)
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record HandoverVO(Long id, Long claimId, Long itemId, String itemTitle, String itemType,
                         Long ownerId, String ownerName, String ownerContact,
                         Long claimantId, String claimantName, String claimantContact,
                         OffsetDateTime handTime, String place, OffsetDateTime createdAt) {
}
