package com.xunwu.leida.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本相似度：中文按二元组切分，英文/数字按单词切分，Dice 系数
 */
public final class TextSimilarity {

    /** 连续的英文字母或数字视为一个单词 */
    private static final Pattern WORD = Pattern.compile("[a-z0-9]+");
    /** 连续的 CJK 汉字片段 */
    private static final Pattern CJK_RUN = Pattern.compile("[一-鿿]+");

    private TextSimilarity() {
    }

    /** 返回 [0,1] 的相似度；任一文本没有有效词元时返回 0 */
    public static double similarity(String a, String b) {
        Set<String> tokensA = tokenize(a);
        Set<String> tokensB = tokenize(b);
        if (tokensA.isEmpty() || tokensB.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(tokensA);
        intersection.retainAll(tokensB);
        // Dice 系数 = 2 * |交集| / (|A| + |B|)
        return 2.0 * intersection.size() / (tokensA.size() + tokensB.size());
    }

    /** 中文二元组 + 英文/数字单词（小写） */
    public static Set<String> tokenize(String text) {
        Set<String> tokens = new HashSet<>();
        if (text == null || text.isBlank()) {
            return tokens;
        }
        String normalized = text.toLowerCase(Locale.ROOT);
        Matcher words = WORD.matcher(normalized);
        while (words.find()) {
            tokens.add(words.group());
        }
        Matcher cjk = CJK_RUN.matcher(normalized);
        while (cjk.find()) {
            String run = cjk.group();
            if (run.length() == 1) {
                tokens.add(run);
            } else {
                for (int i = 0; i + 2 <= run.length(); i++) {
                    tokens.add(run.substring(i, i + 2));
                }
            }
        }
        return tokens;
    }
}
