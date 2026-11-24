package com.xunwu.leida.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 密码哈希纯单元测试:hash 后 verify 成功、错误密码失败(不连数据库)
 */
class PasswordHasherTest {

    @Test
    void hash后verify成功() {
        String stored = PasswordUtil.hash("Passw0rd!");
        assertTrue(PasswordUtil.verify("Passw0rd!", stored));
    }

    @Test
    void 错误密码verify失败() {
        String stored = PasswordUtil.hash("Passw0rd!");
        assertFalse(PasswordUtil.verify("passw0rd!", stored));
        assertFalse(PasswordUtil.verify("", stored));
    }

    @Test
    void 相同密码两次hash不同_盐随机() {
        String a = PasswordUtil.hash("same-password");
        String b = PasswordUtil.hash("same-password");
        assertNotEquals(a, b);
        assertTrue(PasswordUtil.verify("same-password", a));
        assertTrue(PasswordUtil.verify("same-password", b));
    }

    @Test
    void 存储格式为pbkdf2四段() {
        String stored = PasswordUtil.hash("abc123");
        String[] parts = stored.split("\\$");
        assertTrue(parts.length == 4 && "pbkdf2".equals(parts[0]));
        assertFalse(stored.contains("abc123"));
    }

    @Test
    void 非法存储值一律verify失败() {
        assertFalse(PasswordUtil.verify("abc123", null));
        assertFalse(PasswordUtil.verify(null, PasswordUtil.hash("abc123")));
        assertFalse(PasswordUtil.verify("abc123", "plain-text"));
        assertFalse(PasswordUtil.verify("abc123", "pbkdf2$notnum$c2FsdA==$aGFzaA=="));
    }
}
