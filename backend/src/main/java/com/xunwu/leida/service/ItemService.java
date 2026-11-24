package com.xunwu.leida.service;

import com.xunwu.leida.common.BizException;
import com.xunwu.leida.common.PageResult;
import com.xunwu.leida.dto.ItemCreateRequest;
import com.xunwu.leida.dto.ItemDetailVO;
import com.xunwu.leida.dto.ItemUpdateRequest;
import com.xunwu.leida.dto.ItemVO;
import com.xunwu.leida.dto.MyClaimVO;
import com.xunwu.leida.entity.Item;
import com.xunwu.leida.mapper.CategoryMapper;
import com.xunwu.leida.mapper.ItemMapper;
import com.xunwu.leida.mapper.LocationMapper;
import com.xunwu.leida.security.CurrentUser;
import com.xunwu.leida.security.UserContext;
import com.xunwu.leida.util.ContactMasker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemMapper itemMapper;
    private final CategoryMapper categoryMapper;
    private final LocationMapper locationMapper;

    public ItemService(ItemMapper itemMapper, CategoryMapper categoryMapper, LocationMapper locationMapper) {
        this.itemMapper = itemMapper;
        this.categoryMapper = categoryMapper;
        this.locationMapper = locationMapper;
    }

    public PageResult<ItemVO> list(String type, Long categoryId, Long locationId, int page, int pageSize) {
        if (type != null && !type.isEmpty() && !type.equals("lost") && !type.equals("found")) {
            throw new BizException(400, "type 只允许 lost 或 found");
        }
        // 分页参数兜底:page 最小 1,pageSize 默认 10、上限 50
        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 50);
        CurrentUser viewer = UserContext.get();
        long total = itemMapper.count(type, categoryId, locationId);
        List<ItemVO> list = itemMapper
                .findPage(type, categoryId, locationId, (safePage - 1) * safePageSize, safePageSize)
                .stream()
                .map(item -> ItemVO.from(item, visibleContact(item, viewer)))
                .toList();
        return new PageResult<>(list, total, safePage, safePageSize);
    }

    public ItemDetailVO create(ItemCreateRequest req) {
        CurrentUser user = UserContext.require();
        validateRefs(req.categoryId(), req.locationId());
        Item item = new Item();
        item.setPublisherId(user.id());
        item.setTitle(req.title().trim());
        item.setDescription(req.description());
        item.setType(req.type());
        item.setCategoryId(req.categoryId());
        item.setLocationId(req.locationId());
        item.setEventTime(req.eventTime());
        item.setImageUrl(req.imageUrl());
        itemMapper.insert(item);
        return detail(item.getId());
    }

    public ItemDetailVO detail(long id) {
        Item item = requireItem(id);
        CurrentUser viewer = UserContext.get();
        MyClaimVO myClaim = viewer == null ? null : itemMapper.findMyClaim(id, viewer.id());
        return ItemDetailVO.from(item, visibleContact(item, viewer), myClaim);
    }

    public ItemDetailVO update(long id, ItemUpdateRequest req) {
        CurrentUser user = UserContext.require();
        Item item = requireItem(id);
        requireOwnerOrAdmin(item, user);
        if ("completed".equals(item.getStatus()) || "cancelled".equals(item.getStatus())) {
            throw new BizException(409, "已完成或已取消的物品不能编辑");
        }
        validateRefs(req.categoryId(), req.locationId());
        item.setTitle(req.title().trim());
        item.setDescription(req.description());
        item.setCategoryId(req.categoryId());
        item.setLocationId(req.locationId());
        item.setEventTime(req.eventTime());
        item.setImageUrl(req.imageUrl());
        itemMapper.update(item);
        return detail(id);
    }

    public void delete(long id) {
        CurrentUser user = UserContext.require();
        Item item = requireItem(id);
        requireOwnerOrAdmin(item, user);
        // WHERE 已带 deleted_at IS NULL,并发重复删除时影响行数为 0
        if (itemMapper.softDelete(id) == 0) {
            throw new BizException(404, "物品不存在");
        }
    }

    private Item requireItem(long id) {
        Item item = itemMapper.findById(id);
        if (item == null) {
            throw new BizException(404, "物品不存在");
        }
        return item;
    }

    private void requireOwnerOrAdmin(Item item, CurrentUser user) {
        if (!item.getPublisherId().equals(user.id()) && !"admin".equals(user.role())) {
            throw new BizException(403, "只有发布者或管理员可以操作");
        }
    }

    private void validateRefs(Long categoryId, Long locationId) {
        if (categoryId != null && !categoryMapper.existsById(categoryId)) {
            throw new BizException(400, "分类不存在");
        }
        if (locationId != null && !locationMapper.existsById(locationId)) {
            throw new BizException(400, "地点不存在");
        }
    }

    /**
     * 契约第 4 节:匿名 null;发布者本人/管理员/认领已通过给完整值;其余脱敏
     */
    private String visibleContact(Item item, CurrentUser viewer) {
        if (viewer == null) {
            return null;
        }
        boolean fullAccess = item.getPublisherId().equals(viewer.id()) || "admin".equals(viewer.role())
                || itemMapper.hasApprovedClaim(item.getId(), viewer.id());
        return ContactMasker.visible(item.getPublisherContact(), true, fullAccess);
    }
}
