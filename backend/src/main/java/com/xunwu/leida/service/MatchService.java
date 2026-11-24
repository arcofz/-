package com.xunwu.leida.service;

import com.xunwu.leida.common.BizException;
import com.xunwu.leida.dto.MatchVO;
import com.xunwu.leida.entity.Item;
import com.xunwu.leida.entity.Match;
import com.xunwu.leida.entity.Notification;
import com.xunwu.leida.mapper.ItemMapper;
import com.xunwu.leida.mapper.MatchMapper;
import com.xunwu.leida.mapper.NotificationMapper;
import com.xunwu.leida.security.CurrentUser;
import com.xunwu.leida.security.UserContext;
import com.xunwu.leida.util.MatchScoreCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 智能匹配:评分取 Top5 并 upsert 到 matches;确认/拒绝仅失主或管理员(契约第 8 节)
 */
@Service
public class MatchService {

    private final ItemMapper itemMapper;
    private final MatchMapper matchMapper;
    private final NotificationMapper notificationMapper;

    public MatchService(ItemMapper itemMapper, MatchMapper matchMapper, NotificationMapper notificationMapper) {
        this.itemMapper = itemMapper;
        this.matchMapper = matchMapper;
        this.notificationMapper = notificationMapper;
    }

    /** GET /items/{id}/matches:候选为相反 type,评分排序取前 5 */
    @Transactional
    public List<MatchVO> matches(long itemId) {
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BizException(404, "物品不存在");
        }
        String oppositeType = "lost".equals(item.getType()) ? "found" : "lost";
        List<Item> candidates = itemMapper.findMatchCandidates(itemId, oppositeType);

        record Scored(Item candidate, MatchScoreCalculator.Result result) {
        }
        List<Scored> top = candidates.stream()
                .map(c -> new Scored(c, MatchScoreCalculator.calculate(item, c)))
                .sorted(Comparator.comparing((Scored s) -> s.result().score()).reversed()
                        .thenComparing(s -> s.candidate().getId()))
                .limit(5)
                .toList();

        List<MatchVO> result = new ArrayList<>();
        for (Scored s : top) {
            // lost/found 归一化:matches 唯一键固定为 (lost_item_id, found_item_id)
            Long lostId = "lost".equals(item.getType()) ? item.getId() : s.candidate().getId();
            Long foundId = "found".equals(item.getType()) ? item.getId() : s.candidate().getId();
            Match match = matchMapper.upsert(lostId, foundId, s.result().score());
            result.add(MatchVO.of(match, s.candidate(), s.result().reasons()));
        }
        return result;
    }

    @Transactional
    public void confirm(long matchId) {
        decide(matchId, true);
    }

    @Transactional
    public void reject(long matchId) {
        decide(matchId, false);
    }

    private void decide(long matchId, boolean confirm) {
        CurrentUser user = UserContext.require();
        Match match = matchMapper.findById(matchId);
        if (match == null) {
            throw new BizException(404, "匹配记录不存在");
        }
        Item lostItem = itemMapper.findById(match.getLostItemId());
        Item foundItem = itemMapper.findById(match.getFoundItemId());
        if (lostItem == null || foundItem == null) {
            throw new BizException(404, "匹配关联的物品不存在");
        }
        // 契约第 8 节:仅失主(lost item 发布者)或管理员可确认/拒绝
        if (!lostItem.getPublisherId().equals(user.id()) && !"admin".equals(user.role())) {
            throw new BizException(403, "只有失主或管理员可以操作");
        }
        if (!"pending".equals(match.getStatus())) {
            throw new BizException(409, "该匹配已处理");
        }
        // WHERE 带 pending 条件,并发下影响行数为 0 视为冲突
        if (matchMapper.updateStatus(matchId, confirm ? "confirmed" : "rejected") == 0) {
            throw new BizException(409, "该匹配已处理");
        }
        if (confirm) {
            itemMapper.updateStatus(lostItem.getId(), "matched");
            itemMapper.updateStatus(foundItem.getId(), "matched");
        }
        notify(foundItem.getPublisherId(), confirm ? "match_confirmed" : "match_rejected",
                confirm ? "失主已确认「" + foundItem.getTitle() + "」与「" + lostItem.getTitle() + "」的匹配"
                        : "失主已拒绝「" + foundItem.getTitle() + "」与「" + lostItem.getTitle() + "」的匹配");
    }

    private void notify(Long userId, String type, String content) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setContent(content);
        notificationMapper.insert(n);
    }
}
