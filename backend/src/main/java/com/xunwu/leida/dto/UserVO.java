package com.xunwu.leida.dto;

import com.xunwu.leida.entity.User;

import java.time.OffsetDateTime;

/**
 * 用户对外视图，绝不包含 passwordHash
 */
public record UserVO(Long id, String studentNo, String name, String role,
                     String contact, OffsetDateTime createdAt) {

    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getStudentNo(), user.getName(),
                user.getRole(), user.getContact(), user.getCreatedAt());
    }
}
