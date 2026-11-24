package com.xunwu.leida.util;

/**
 * 联系方式裁剪（契约第 4 节，纯函数）：
 * 匿名 null；无权查看给脱敏值；发布者本人/管理员/认领已通过给完整值
 */
public final class ContactMasker {

    private ContactMasker() {
    }

    public static String visible(String contact, boolean loggedIn, boolean fullAccess) {
        if (!loggedIn || contact == null || contact.isEmpty()) {
            return null;
        }
        return fullAccess ? contact : mask(contact);
    }

    /** 保留前 3 位与后 4 位，中间用 ****；长度不足 8 位时按实际长度安全截断 */
    public static String mask(String contact) {
        if (contact == null || contact.isEmpty()) {
            return null;
        }
        int len = contact.length();
        if (len <= 4) {
            return "****";
        }
        int head = Math.min(3, len - 4);
        return contact.substring(0, head) + "****" + contact.substring(len - 4);
    }
}
