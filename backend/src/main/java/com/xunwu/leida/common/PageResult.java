package com.xunwu.leida.common;

import java.util.List;

/**
 * 分页列表统一结构 { list, total, page, pageSize }
 */
public record PageResult<T>(List<T> list, long total, int page, int pageSize) {
}
