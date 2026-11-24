package com.xunwu.leida.service;

import com.xunwu.leida.common.BizException;
import com.xunwu.leida.dto.HandoverCreateRequest;
import com.xunwu.leida.dto.HandoverVO;
import com.xunwu.leida.entity.Claim;
import com.xunwu.leida.entity.Handover;
import com.xunwu.leida.entity.Item;
import com.xunwu.leida.entity.Notification;
import com.xunwu.leida.mapper.ClaimMapper;
import com.xunwu.leida.mapper.HandoverMapper;
import com.xunwu.leida.mapper.ItemMapper;
import com.xunwu.leida.mapper.NotificationMapper;
import com.xunwu.leida.security.CurrentUser;
import com.xunwu.leida.security.UserContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * 交接:补登记与详情;详情仅认领人/物品发布者/管理员可见(契约第 6 节)
 */
@Service
public class HandoverService {

    private final HandoverMapper handoverMapper;
    private final ClaimMapper claimMapper;
    private final ItemMapper itemMapper;
    private final NotificationMapper notificationMapper;

    public HandoverService(HandoverMapper handoverMapper, ClaimMapper claimMapper,
                           ItemMapper itemMapper, NotificationMapper notificationMapper) {
        this.handoverMapper = handoverMapper;
        this.claimMapper = claimMapper;
        this.itemMapper = itemMapper;
        this.notificationMapper = notificationMapper;
    }

    /** POST /handovers:仅 approved 且尚无交接的认领可补登记 */
    @Transactional
    public HandoverVO create(HandoverCreateRequest req) {
        CurrentUser user = UserContext.require();
        Claim claim = claimMapper.findById(req.claimId());
        if (claim == null) {
            throw new BizException(404, "认领记录不存在");
        }
        Item item = requireItem(claim.getItemId());
        // 物品发布者、认领人、管理员都可以补登记
        if (!item.getPublisherId().equals(user.id())
                && !claim.getClaimantId().equals(user.id())
                && !"admin".equals(user.role())) {
            throw new BizException(403, "只有物品发布者、认领人或管理员可以操作");
        }
        if (!"approved".equals(claim.getStatus())) {
            throw new BizException(409, "只有已通过的认领可以登记交接");
        }
        if (handoverMapper.findByClaimId(claim.getId()) != null) {
            throw new BizException(409, "该认领已登记交接");
        }
        Handover handover = insertHandover(claim.getId(), req.handTime(), req.place());
        itemMapper.updateStatus(item.getId(), "completed");
        // 通知对方:操作人是认领人则通知发布者,反之通知认领人
        Long recipient = claim.getClaimantId().equals(user.id())
                ? item.getPublisherId() : claim.getClaimantId();
        notify(recipient, "handover_created",
                "「" + item.getTitle() + "」已登记交接:" + req.place());
        return detail(handover.getId());
    }

    /** GET /handovers/{id}:联系方式给完整值,权限在这里强制 */
    public HandoverVO detail(long id) {
        CurrentUser user = UserContext.require();
        HandoverVO vo = handoverMapper.findVOById(id);
        if (vo == null) {
            throw new BizException(404, "交接记录不存在");
        }
        if (!vo.claimantId().equals(user.id())
                && !vo.ownerId().equals(user.id())
                && !"admin".equals(user.role())) {
            throw new BizException(403, "只有认领人、物品发布者或管理员可以查看");
        }
        return vo;
    }

    /** 仅插入交接行;approve 带交接信息时复用,物品状态与通知由调用方处理 */
    public Handover insertHandover(Long claimId, OffsetDateTime handTime, String place) {
        Handover handover = new Handover();
        handover.setClaimId(claimId);
        handover.setHandTime(handTime);
        handover.setPlace(place.trim());
        try {
            handoverMapper.insert(handover);
        } catch (DuplicateKeyException e) {
            // claim_id 唯一约束兜底并发重复登记
            throw new BizException(409, "该认领已登记交接");
        }
        return handover;
    }

    private Item requireItem(Long itemId) {
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BizException(404, "物品不存在");
        }
        return item;
    }

    private void notify(Long userId, String type, String content) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setContent(content);
        notificationMapper.insert(n);
    }
}
