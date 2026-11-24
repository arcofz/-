package com.xunwu.leida.dto;

/**
 * 注册/登录成功响应 { token, user }
 */
public record AuthResponse(String token, UserVO user) {
}
