package com.xunwu.leida.security;

import com.xunwu.leida.common.BizException;

/**
 * ThreadLocal 保存当前登录用户，请求结束必须 clear
 */
public final class UserContext {

    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(CurrentUser user) {
        HOLDER.set(user);
    }

    /** 未登录返回 null */
    public static CurrentUser get() {
        return HOLDER.get();
    }

    /** 取当前用户，未登录抛 401 */
    public static CurrentUser require() {
        CurrentUser user = HOLDER.get();
        if (user == null) {
            throw new BizException(401, "未登录或登录已过期");
        }
        return user;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
