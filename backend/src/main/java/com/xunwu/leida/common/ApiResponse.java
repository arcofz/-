package com.xunwu.leida.common;

import com.fasterxml.jackson.annotation.JsonInclude;

// 全局配置了 non_null，这里强制 ALWAYS，保证出错时 data:null 不被吞掉
@JsonInclude(JsonInclude.Include.ALWAYS)
public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "ok", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
