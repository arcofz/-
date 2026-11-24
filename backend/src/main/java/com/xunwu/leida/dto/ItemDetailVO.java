package com.xunwu.leida.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.xunwu.leida.entity.Item;

import java.time.OffsetDateTime;

/**
 * 物品详情:字段与列表一致,额外携带 myClaim。
 * ALWAYS 覆盖全局 non_null,保证 myClaim 没有认领/匿名时也返回 null
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record ItemDetailVO(Long id, String title, String description, String type, String status,
                           Long categoryId, String categoryName, Long locationId, String locationName,
                           OffsetDateTime eventTime, String imageUrl,
                           Long publisherId, String publisherName, String publisherContact,
                           OffsetDateTime createdAt, MyClaimVO myClaim) {

    public static ItemDetailVO from(Item item, String contact, MyClaimVO myClaim) {
        return new ItemDetailVO(item.getId(), item.getTitle(), item.getDescription(),
                item.getType(), item.getStatus(),
                item.getCategoryId(), item.getCategoryName(),
                item.getLocationId(), item.getLocationName(),
                item.getEventTime(), item.getImageUrl(),
                item.getPublisherId(), item.getPublisherName(), contact,
                item.getCreatedAt(), myClaim);
    }
}
