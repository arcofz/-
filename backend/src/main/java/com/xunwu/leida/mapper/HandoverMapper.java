package com.xunwu.leida.mapper;

import com.xunwu.leida.dto.HandoverVO;
import com.xunwu.leida.entity.Handover;
import org.apache.ibatis.annotations.Param;

public interface HandoverMapper {

    int insert(Handover handover);

    Handover findByClaimId(@Param("claimId") Long claimId);

    /** 契约第 6 节完整字段(联表带出物品与双方联系方式) */
    HandoverVO findVOById(@Param("id") Long id);
}
