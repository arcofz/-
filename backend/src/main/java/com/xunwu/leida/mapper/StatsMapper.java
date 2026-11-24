package com.xunwu.leida.mapper;

import com.xunwu.leida.dto.StatsVO;

public interface StatsMapper {

    /** 后台统计:一次查询取全部计数 */
    StatsVO stats();
}
