package com.xunwu.leida.dto;

import com.xunwu.leida.entity.Item;

import java.time.OffsetDateTime;

/**
 * 物品列表项视图;publisherContact 已由后端按可见性规则裁剪
 */
public record ItemVO(Long id, String title, String description, String type, String status,
                     Long categoryId, String categoryName, Long locationId, String locationName,
                     OffsetDateTime eventTime, String imageUrl,
                     Long publisherId, String publisherName, String publisherContact,
                     OffsetDateTime createdAt) {

    public static ItemVO from(Item item, String contact) {
        return new ItemVO(item.getId(), item.getTitle(), item.getDescription(),
                item.getType(), item.getStatus(),
                item.getCategoryId(), item.getCategoryName(),
                item.getLocationId(), item.getLocationName(),
                item.getEventTime(), item.getImageUrl(),
                item.getPublisherId(), item.getPublisherName(), contact,
                item.getCreatedAt());
    }
}
