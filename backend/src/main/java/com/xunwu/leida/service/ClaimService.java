package com.xunwu.leida.service;

import com.xunwu.leida.common.BizException;
import com.xunwu.leida.common.PageResult;
import com.xunwu.leida.dto.ClaimApproveRequest;
import com.xunwu.leida.dto.ClaimApproveVO;
import com.xunwu.leida.dto.ClaimCreateRequest;
import com.xunwu.leida.dto.ClaimRejectRequest;
import com.xunwu.leida.dto.ClaimVO;
import com.xunwu.leida.dto.HandoverVO;
import com.xunwu.leida.entity.Claim;
import com.xunwu.leida.entity.Handover;
import com.xunwu.leida.entity.Item;
import com.xunwu.leida.entity.Notification;
import com.xunwu.leida.mapper.ClaimMapper;
import com.xunwu.leida.mapper.ItemMapper;
import com.xunwu.leida.mapper.NotificationMapper;
import com.xunwu.leida.security.CurrentUser;
import com.xunwu.leida.security.UserContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 认领:提交、我的认领列表、审核通过(可带交接信息,契约第 3 节)、驳回(契约第 5 节)
 */
@Service
public class ClaimService {

    private static final Set<String> CLAIM_STATUSES = Set.of("pending", "approved", "rejected");

    private final ClaimMapper claimMapper;
    private final ItemMapper itemMapper;
    private final NotificationMapper notificationMapper;
    private final HandoverService handoverService;

    public ClaimService(ClaimMapper claimMapper, ItemMapper itemMapper,
                        NotificationMapper notificationMapper, HandoverService handoverService) {
        this.claimMapper = claimMapper;
        this.itemMapper = itemMapper;
        this.notificationMapper = notificationMapper;
        this.handoverService = handoverService;
    }

    /** POST /claims:只允许认领 found 物品,不能认领自己发布的,重复认领 409 */
    @Transactional
    public ClaimVO create(ClaimCreateRequest req) {
        CurrentUser user = UserContext.require();
        Item item = itemMapper.findById(req.itemId());
        if (item == null) {
            throw new BizException(404, "物品不存在");
        }
        if (!"found".equals(item.getType())) {
            throw new BizException(400, "只能认领拾物(found)信息");
        }
        if (item.getPublisherId().equals(user.id())) {
            throw new BizException(400, "不能认领自己发布的物品");
        }
        Claim claim = new Claim();
        claim.setItemId(item.getId());
        claim.setClaimantId(user.id());
        claim.setEvidence(req.evidence().trim());
        try {
            claimMapper.insert(claim);
        } catch (DuplicateKeyException e) {
            // item_id + claimant_id 唯一约束兜底并发重复认领
            throw new BizException(409, "你已认领过该物品");
        }
        notify(item.getPublisherId(), "claim_created",
                "你的物品「" + item.getTitle() + "」收到新的认领申请");
        return claimMapper.findVOById(claim.getId());
    }

    /** GET /claims/mine:role=all 取我发起的 + 我收到的并集 */
    public PageResult<ClaimVO> mine(String role, String status, int page, int pageSize) {
        CurrentUser user = UserContext.require();
        if (role == null || role.isEmpty()) {
            role = "all";
        }
        if (!Set.of("all", "claimant", "owner").contains(role)) {
            throw new BizException(400, "role 只允许 all、claimant 或 owner");
        }
        if (status != null && !status.isEmpty() && !CLAIM_STATUSES.contains(status)) {
            throw new BizException(400, "status 只允许 pending、approved 或 rejected");
        }
        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 50);
        long total = claimMapper.countMine(user.id(), role, status);
        List<ClaimVO> list = claimMapper.findMine(user.id(), role, status,
                (safePage - 1) * safePageSize, safePageSize);
        return new PageResult<>(list, total, safePage, safePageSize);
    }

    /** POST /claims/{id}/approve:一个事务里审核 + 可选交接(契约第 3 节) */
    @Transactional
    public ClaimApproveVO approve(long id, ClaimApproveRequest req) {
        CurrentUser user = UserContext.require();
        boolean hasTime = req != null && req.handTime() != null;
        boolean hasPlace = req != null && req.place() != null && !req.place().isBlank();
        // 只传一个 → 400
        if (hasTime != hasPlace) {
            throw new BizException(400, "handTime 与 place 必须同时提供");
        }
        Claim claim = requireClaim(id);
        Item item = requireItem(claim.getItemId());
        requireOwnerOrAdmin(item, user);
        requirePending(claim);
        // WHERE 带 pending,并发重复审核时影响行数为 0
        if (claimMapper.review(id, "approved", null) == 0) {
            throw new BizException(409, "该认领已处理");
        }
        HandoverVO handoverVO = null;
        if (hasTime) {
            Handover handover = handoverService.insertHandover(id, req.handTime(), req.place());
            itemMapper.updateStatus(item.getId(), "completed");
            handoverVO = handoverService.detail(handover.getId());
        } else {
            itemMapper.updateStatus(item.getId(), "claimed");
        }
        notify(claim.getClaimantId(), "claim_approved",
                hasTime ? "你对「" + item.getTitle() + "」的认领已通过,交接地点:" + req.place().trim()
                        : "你对「" + item.getTitle() + "」的认领已通过,请等待交接安排");
        return new ClaimApproveVO(claimMapper.findVOById(id), handoverVO);
    }

    /** POST /claims/{id}/reject:reason 必填 1–200 字(校验注解保证),写入通知 */
    @Transactional
    public ClaimVO reject(long id, ClaimRejectRequest req) {
        CurrentUser user = UserContext.require();
        Claim claim = requireClaim(id);
        Item item = requireItem(claim.getItemId());
        requireOwnerOrAdmin(item, user);
        requirePending(claim);
        String reason = req.reason().trim();
        if (reason.isEmpty()) {
            throw new BizException(400, "驳回理由不能为空");
        }
        if (claimMapper.review(id, "rejected", reason) == 0) {
            throw new BizException(409, "该认领已处理");
        }
        notify(claim.getClaimantId(), "claim_rejected",
                "你对「" + item.getTitle() + "」的认领被驳回:" + reason);
        return claimMapper.findVOById(id);
    }

    private Claim requireClaim(long id) {
        Claim claim = claimMapper.findById(id);
        if (claim == null) {
            throw new BizException(404, "认领记录不存在");
        }
        return claim;
    }

    private Item requireItem(Long itemId) {
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BizException(404, "物品不存在");
        }
        return item;
    }

    private void requireOwnerOrAdmin(Item item, CurrentUser user) {
        if (!item.getPublisherId().equals(user.id()) && !"admin".equals(user.role())) {
            throw new BizException(403, "只有物品发布者或管理员可以操作");
        }
    }

    private void requirePending(Claim claim) {
        if (!"pending".equals(claim.getStatus())) {
            throw new BizException(409, "该认领已处理");
        }
    }

    private void notify(Long userId, String type, String content) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setContent(content);
        notificationMapper.insert(n);
    }
}
