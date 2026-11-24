package com.xunwu.leida.dto;

import com.xunwu.leida.entity.Notification;

import java.time.OffsetDateTime;

/**
 * 通知对外视图 { id, type, content, isRead, createdAt }
 */
public record NotificationVO(Long id, String type, String content,
                             Boolean isRead, OffsetDateTime createdAt) {

    public static NotificationVO from(Notification n) {
        return new NotificationVO(n.getId(), n.getType(), n.getContent(),
                n.getIsRead(), n.getCreatedAt());
    }
}
