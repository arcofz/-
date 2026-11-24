package com.xunwu.leida.mapper;

import com.xunwu.leida.entity.Match;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface MatchMapper {

    /** upsert:唯一键冲突时更新分数并回到 pending,返回行(含 id) */
    Match upsert(@Param("lostItemId") Long lostItemId,
                 @Param("foundItemId") Long foundItemId,
                 @Param("score") BigDecimal score);

    Match findById(@Param("id") Long id);

    /** 仅 pending 状态可流转;返回影响行数,0 表示已被处理 */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
