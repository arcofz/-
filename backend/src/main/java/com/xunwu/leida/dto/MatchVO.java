package com.xunwu.leida.dto;

import com.xunwu.leida.entity.Item;
import com.xunwu.leida.entity.Match;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * GET /items/{id}/matches 数组元素:候选物品 + 匹配分数与理由
 */
public record MatchVO(Long matchId, Long itemId, String title, String type,
                      Long categoryId, String categoryName, Long locationId, String locationName,
                      OffsetDateTime eventTime, String imageUrl,
                      BigDecimal score, List<String> reasons, String status) {

    public static MatchVO of(Match match, Item candidate, List<String> reasons) {
        return new MatchVO(match.getId(), candidate.getId(), candidate.getTitle(), candidate.getType(),
                candidate.getCategoryId(), candidate.getCategoryName(),
                candidate.getLocationId(), candidate.getLocationName(),
                candidate.getEventTime(), candidate.getImageUrl(),
                match.getScore(), reasons, match.getStatus());
    }
}
