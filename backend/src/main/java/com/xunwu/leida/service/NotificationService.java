package com.xunwu.leida.service;

import com.xunwu.leida.common.BizException;
import com.xunwu.leida.common.PageResult;
import com.xunwu.leida.dto.NotificationVO;
import com.xunwu.leida.entity.Notification;
import com.xunwu.leida.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public PageResult<NotificationVO> list(long userId, int page, int pageSize, boolean unreadOnly) {
        // 分页参数兜底，避免非法值导致 SQL 报错
        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        long total = notificationMapper.countByUser(userId, unreadOnly);
        List<NotificationVO> list = notificationMapper
                .findByUser(userId, unreadOnly, (safePage - 1) * safePageSize, safePageSize)
                .stream()
                .map(NotificationVO::from)
                .toList();
        return new PageResult<>(list, total, safePage, safePageSize);
    }

    public void markRead(long userId, long notificationId) {
        Notification notification = notificationMapper.findById(notificationId);
        if (notification == null) {
            throw new BizException(404, "通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BizException(403, "无权操作他人通知");
        }
        notificationMapper.markRead(notificationId);
    }
}
