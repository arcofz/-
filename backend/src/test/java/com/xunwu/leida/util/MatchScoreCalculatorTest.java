package com.xunwu.leida.util;

import com.xunwu.leida.entity.Item;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 匹配评分纯单元测试:分类 40 / 地点 25(同楼 15) / 时间 20 / 文本 15,总分保留 2 位
 */
class MatchScoreCalculatorTest {

    private static final OffsetDateTime BASE = OffsetDateTime.of(2026, 11, 24, 12, 0, 0, 0, ZoneOffset.ofHours(8));

    private Item item(Long categoryId, Long locationId, String building, OffsetDateTime eventTime,
                      String title, String description) {
        Item item = new Item();
        item.setCategoryId(categoryId);
        item.setLocationId(locationId);
        item.setBuilding(building);
        item.setEventTime(eventTime);
        item.setTitle(title);
        item.setDescription(description);
        return item;
    }

    @Test
    void 分类一致得40分() {
        Item a = item(1L, null, null, BASE, "甲", null);
        Item b = item(1L, null, null, BASE.plusYears(1), "乙", null);
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertEquals(new BigDecimal("40.00"), r.score()); // 仅分类 40,时间超 30 天不得分
        assertTrue(r.reasons().contains("分类一致 +40"));
    }

    @Test
    void 分类不同不得分类分() {
        Item a = item(1L, null, null, BASE, "甲", null);
        Item b = item(2L, null, null, BASE, "乙", null);
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertEquals(new BigDecimal("20.00"), r.score()); // 仅时间 20
        assertFalse(r.reasons().contains("分类一致 +40"));
    }

    @Test
    void 同一地点得25分() {
        Item a = item(null, 5L, "图书馆", BASE, "甲", null);
        Item b = item(null, 5L, "图书馆", BASE, "乙", null);
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertEquals(new BigDecimal("45.00"), r.score()); // 地点 25 + 时间 20
        assertTrue(r.reasons().contains("同一地点 +25"));
    }

    @Test
    void 不同地点但同楼得15分() {
        Item a = item(null, 5L, "图书馆", BASE, "甲", null);
        Item b = item(null, 6L, "图书馆", BASE, "乙", null);
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertEquals(new BigDecimal("35.00"), r.score()); // 同楼 15 + 时间 20
        assertTrue(r.reasons().contains("同一栋楼 +15"));
    }

    @Test
    void 地点不同且不同楼不得地点分() {
        Item a = item(null, 5L, "图书馆", BASE, "甲", null);
        Item b = item(null, 6L, "教学楼", BASE, "乙", null);
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertEquals(new BigDecimal("20.00"), r.score());
    }

    @Test
    void 时间分段24小时内20分72小时内15分7天内10分30天内5分() {
        Item a = item(null, null, null, BASE, "甲", null);
        assertEquals(new BigDecimal("20.00"),
                MatchScoreCalculator.calculate(a, item(null, null, null, BASE.plusHours(24), "乙", null)).score());
        assertEquals(new BigDecimal("15.00"),
                MatchScoreCalculator.calculate(a, item(null, null, null, BASE.plusHours(72), "乙", null)).score());
        assertEquals(new BigDecimal("10.00"),
                MatchScoreCalculator.calculate(a, item(null, null, null, BASE.plusDays(7), "乙", null)).score());
        assertEquals(new BigDecimal("5.00"),
                MatchScoreCalculator.calculate(a, item(null, null, null, BASE.plusDays(30), "乙", null)).score());
        assertEquals(new BigDecimal("0.00"),
                MatchScoreCalculator.calculate(a, item(null, null, null, BASE.plusDays(31), "乙", null)).score());
    }

    @Test
    void 时间差取绝对值与方向无关() {
        Item a = item(null, null, null, BASE, "甲", null);
        Item b = item(null, null, null, BASE.minusHours(3), "乙", null);
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertEquals(new BigDecimal("20.00"), r.score());
        assertTrue(r.reasons().contains("时间相差 3 小时 +20"));
    }

    @Test
    void 文本相似度乘15并保留2位() {
        // "黑色雨伞" vs "黑色背包" Dice = 1/3,文本分 = 5.00
        Item a = item(null, null, null, BASE, "黑色雨伞", null);
        Item b = item(null, null, null, BASE.plusYears(1), "黑色背包", null);
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertEquals(new BigDecimal("5.00"), r.score());
        assertTrue(r.reasons().stream().anyMatch(s -> s.startsWith("文本相似度 0.33 +5.00")));
    }

    @Test
    void 四个维度全满分总分100() {
        Item a = item(1L, 5L, "图书馆", BASE, "黑色雨伞", "伞柄有蓝色挂绳");
        Item b = item(1L, 5L, "图书馆", BASE.plusHours(1), "黑色雨伞", "伞柄有蓝色挂绳");
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertEquals(new BigDecimal("100.00"), r.score());
        assertEquals(4, r.reasons().size());
    }

    @Test
    void 总分不超过100且保留2位小数() {
        Item a = item(1L, 5L, "图书馆", BASE, "黑色雨伞", null);
        Item b = item(1L, 5L, "图书馆", BASE, "黑色雨伞", null);
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertTrue(r.score().compareTo(new BigDecimal("100.00")) <= 0);
        assertEquals(2, r.score().scale());
    }

    @Test
    void 全部不匹配得0分且无理由() {
        Item a = item(1L, 5L, "图书馆", BASE, "雨伞", null);
        Item b = item(2L, 6L, "教学楼", BASE.plusDays(60), "钥匙", null);
        MatchScoreCalculator.Result r = MatchScoreCalculator.calculate(a, b);
        assertEquals(new BigDecimal("0.00"), r.score());
        assertTrue(r.reasons().isEmpty());
    }
}
