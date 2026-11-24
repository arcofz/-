package com.xunwu.leida.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 文本相似度纯单元测试:中文二元组、英文/数字单词、Dice 系数
 */
class TextSimilarityTest {

    @Test
    void 完全相同的中文文本相似度为1() {
        assertEquals(1.0, TextSimilarity.similarity("黑色雨伞", "黑色雨伞"), 1e-9);
    }

    @Test
    void 中文按二元组计算Dice系数() {
        // "黑色雨伞" -> {黑色,色雨,雨伞};"黑色背包" -> {黑色,色背,背包};交集 {黑色}
        assertEquals(2.0 / 6, TextSimilarity.similarity("黑色雨伞", "黑色背包"), 1e-9);
    }

    @Test
    void 英文按单词且忽略大小写() {
        assertEquals(1.0, TextSimilarity.similarity("Black Umbrella", "black umbrella"), 1e-9);
        // {black,umbrella} vs {black,iphone}:交集 {black}
        assertEquals(0.5, TextSimilarity.similarity("black umbrella", "black iphone"), 1e-9);
    }

    @Test
    void 数字按单词切分() {
        Set<String> tokens = TextSimilarity.tokenize("iPhone 13 Pro");
        assertEquals(Set.of("iphone", "13", "pro"), tokens);
    }

    @Test
    void 中英混合分别切分() {
        // 中文走二元组,连续的英文字母+数字视为一个单词
        Set<String> tokens = TextSimilarity.tokenize("黑色iPhone13");
        assertEquals(Set.of("黑色", "iphone13"), tokens);
    }

    @Test
    void 无共同词元相似度为0() {
        assertEquals(0.0, TextSimilarity.similarity("雨伞", "钥匙"), 1e-9);
    }

    @Test
    void 空文本或null相似度为0() {
        assertEquals(0.0, TextSimilarity.similarity(null, "雨伞"), 1e-9);
        assertEquals(0.0, TextSimilarity.similarity("雨伞", ""), 1e-9);
        assertEquals(0.0, TextSimilarity.similarity("   ", "雨伞"), 1e-9);
    }

    @Test
    void 相似度始终在0到1之间() {
        double s = TextSimilarity.similarity("图书馆一楼捡到黑色雨伞一把", "黑色雨伞 图书馆");
        assertTrue(s >= 0 && s <= 1);
    }
}
