package com.xunwu.leida.controller;

import com.xunwu.leida.common.ApiResponse;
import com.xunwu.leida.common.PageResult;
import com.xunwu.leida.dto.NotificationVO;
import com.xunwu.leida.security.UserContext;
import com.xunwu.leida.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<PageResult<NotificationVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        long userId = UserContext.require().id();
        return ApiResponse.ok(notificationService.list(userId, page, pageSize, unreadOnly));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable long id) {
        long userId = UserContext.require().id();
        notificationService.markRead(userId, id);
        return ApiResponse.ok(null);
    }
}
