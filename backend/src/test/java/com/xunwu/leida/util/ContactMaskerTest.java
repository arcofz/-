package com.xunwu.leida.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 联系方式裁剪纯单元测试:匿名 null / 脱敏 / 完整值 / 短号码
 */
class ContactMaskerTest {

    @Test
    void 匿名访问返回null() {
        assertNull(ContactMasker.visible("13811112222", false, false));
        assertNull(ContactMasker.visible("13811112222", false, true));
    }

    @Test
    void 登录但无完整权限返回脱敏值() {
        assertEquals("138****2222", ContactMasker.visible("13811112222", true, false));
    }

    @Test
    void 有完整权限返回原值() {
        assertEquals("13811112222", ContactMasker.visible("13811112222", true, true));
    }

    @Test
    void 联系方式为空返回null() {
        assertNull(ContactMasker.visible(null, true, false));
        assertNull(ContactMasker.visible("", true, true));
    }

    @Test
    void 标准11位手机号保留前3后4() {
        assertEquals("138****2222", ContactMasker.mask("13811112222"));
    }

    @Test
    void 短号码安全截断() {
        assertEquals("****", ContactMasker.mask("1234"));
        assertEquals("****", ContactMasker.mask("123"));
        assertEquals("1****2345", ContactMasker.mask("12345"));
        assertEquals("12****3456", ContactMasker.mask("123456"));
        assertEquals("123****4567", ContactMasker.mask("1234567"));
        assertEquals("123****5678", ContactMasker.mask("12345678"));
    }

    @Test
    void mask空值返回null() {
        assertNull(ContactMasker.mask(null));
        assertNull(ContactMasker.mask(""));
    }
}
