package com.xunwu.leida.security;

/**
 * 当前登录用户（来自 JWT）
 */
public record CurrentUser(Long id, String role) {
}
