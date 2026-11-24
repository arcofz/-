package com.xunwu.leida.mapper;

import com.xunwu.leida.dto.ClaimVO;
import com.xunwu.leida.entity.Claim;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClaimMapper {

    int insert(Claim claim);

    Claim findById(@Param("id") Long id);

    /** 契约第 2 节完整字段(联表带出物品、双方、handoverId) */
    ClaimVO findVOById(@Param("id") Long id);

    /** role=all|claimant|owner;all 用 OR 取并集,天然去重 */
    List<ClaimVO> findMine(@Param("userId") Long userId,
                           @Param("role") String role,
                           @Param("status") String status,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    long countMine(@Param("userId") Long userId,
                   @Param("role") String role,
                   @Param("status") String status);

    /** 仅 pending 可审核;approve 时 reason 为 null,reject 时写驳回理由 */
    int review(@Param("id") Long id,
               @Param("status") String status,
               @Param("reason") String reason);
}
