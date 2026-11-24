package com.xunwu.leida.mapper;

import com.xunwu.leida.dto.MyClaimVO;
import com.xunwu.leida.entity.Item;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemMapper {

    List<Item> findPage(@Param("type") String type,
                        @Param("categoryId") Long categoryId,
                        @Param("locationId") Long locationId,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

    long count(@Param("type") String type,
               @Param("categoryId") Long categoryId,
               @Param("locationId") Long locationId);

    Item findById(@Param("id") Long id);

    int insert(Item item);

    int update(Item item);

    int softDelete(@Param("id") Long id);

    /** 当前用户对该物品的认领记录,没有则 null;认领模块复用 */
    MyClaimVO findMyClaim(@Param("itemId") Long itemId, @Param("userId") Long userId);

    /** 当前用户对该物品是否有已通过的认领;认领模块复用 */
    boolean hasApprovedClaim(@Param("itemId") Long itemId, @Param("userId") Long userId);

    /** 匹配候选:相反 type、未删、非 cancelled/completed,排除自身 */
    List<Item> findMatchCandidates(@Param("id") Long id, @Param("type") String type);

    /** 仅更新状态(匹配确认/认领审核/交接完成时调用) */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
