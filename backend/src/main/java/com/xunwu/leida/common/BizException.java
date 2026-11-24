package com.xunwu.leida.common;

/**
 * 业务异常：code 即 HTTP 状态码（400/401/403/404/409/500）
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
