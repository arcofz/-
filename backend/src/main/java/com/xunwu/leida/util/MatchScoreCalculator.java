package com.xunwu.leida.util;

import com.xunwu.leida.entity.Item;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 匹配评分（纯函数）：分类 40 + 地点 25 + 时间 20 + 文本 15，总分四舍五入保留 2 位
 */
public final class MatchScoreCalculator {

    private MatchScoreCalculator() {
    }

    /** 评分结果：score 已保留 2 位小数，similarity 为原始文本相似度 */
    public record Result(BigDecimal score, List<String> reasons, double similarity) {
    }

    public static Result calculate(Item lost, Item found) {
        List<String> reasons = new ArrayList<>();
        double total = 0;

        // 分类：相同 +40
        if (lost.getCategoryId() != null && lost.getCategoryId().equals(found.getCategoryId())) {
            total += 40;
            reasons.add("分类一致 +40");
        }

        // 地点：同一地点 +25；不同地点但同楼 +15
        if (lost.getLocationId() != null && lost.getLocationId().equals(found.getLocationId())) {
            total += 25;
            reasons.add("同一地点 +25");
        } else if (lost.getBuilding() != null && lost.getBuilding().equals(found.getBuilding())) {
            total += 15;
            reasons.add("同一栋楼 +15");
        }

        // 时间：按事件时间差分段给分
        double timeScore = 0;
        if (lost.getEventTime() != null && found.getEventTime() != null) {
            Duration gap = Duration.between(lost.getEventTime(), found.getEventTime()).abs();
            if (gap.compareTo(Duration.ofHours(24)) <= 0) {
                timeScore = 20;
            } else if (gap.compareTo(Duration.ofHours(72)) <= 0) {
                timeScore = 15;
            } else if (gap.compareTo(Duration.ofDays(7)) <= 0) {
                timeScore = 10;
            } else if (gap.compareTo(Duration.ofDays(30)) <= 0) {
                timeScore = 5;
            }
            if (timeScore > 0) {
                reasons.add("时间相差 " + humanizeGap(gap) + " +" + (int) timeScore);
            }
        }
        total += timeScore;

        // 文本：title + description 的 Dice 相似度 * 15
        double similarity = TextSimilarity.similarity(
                joinText(lost.getTitle(), lost.getDescription()),
                joinText(found.getTitle(), found.getDescription()));
        double textScore = similarity * 15;
        if (textScore > 0) {
            reasons.add(String.format("文本相似度 %.2f +%s", similarity,
                    BigDecimal.valueOf(textScore).setScale(2, RoundingMode.HALF_UP).toPlainString()));
        }
        total += textScore;

        // 封顶 100（与 matches.score CHECK 约束一致），四舍五入保留 2 位
        BigDecimal score = BigDecimal.valueOf(Math.min(total, 100.0)).setScale(2, RoundingMode.HALF_UP);
        return new Result(score, reasons, similarity);
    }

    private static String joinText(String title, String description) {
        return Objects.requireNonNullElse(title, "") + " " + Objects.requireNonNullElse(description, "");
    }

    /** 时间差的中文短句：不足 1 小时按分钟，不足 24 小时按小时，其余按天 */
    private static String humanizeGap(Duration gap) {
        long minutes = gap.toMinutes();
        if (minutes < 60) {
            return Math.max(minutes, 0) + " 分钟";
        }
        long hours = gap.toHours();
        if (hours < 24) {
            return hours + " 小时";
        }
        return gap.toDays() + " 天";
    }
}
