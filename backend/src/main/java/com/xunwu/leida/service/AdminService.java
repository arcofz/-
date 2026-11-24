package com.xunwu.leida.service;

import com.xunwu.leida.common.BizException;
import com.xunwu.leida.common.PageResult;
import com.xunwu.leida.dto.StatsVO;
import com.xunwu.leida.dto.UserVO;
import com.xunwu.leida.entity.Category;
import com.xunwu.leida.entity.Location;
import com.xunwu.leida.mapper.CategoryMapper;
import com.xunwu.leida.mapper.LocationMapper;
import com.xunwu.leida.mapper.StatsMapper;
import com.xunwu.leida.mapper.UserMapper;
import com.xunwu.leida.security.CurrentUser;
import com.xunwu.leida.security.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台管理:所有接口仅管理员可用
 */
@Service
public class AdminService {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final LocationMapper locationMapper;
    private final StatsMapper statsMapper;

    public AdminService(UserMapper userMapper, CategoryMapper categoryMapper,
                        LocationMapper locationMapper, StatsMapper statsMapper) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.locationMapper = locationMapper;
        this.statsMapper = statsMapper;
    }

    public PageResult<UserVO> users(String keyword, int page, int pageSize) {
        requireAdmin();
        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 50);
        String kw = keyword == null ? null : keyword.trim();
        long total = userMapper.count(kw);
        // UserVO 不含 passwordHash
        List<UserVO> list = userMapper.findPage(kw, (safePage - 1) * safePageSize, safePageSize)
                .stream()
                .map(UserVO::from)
                .toList();
        return new PageResult<>(list, total, safePage, safePageSize);
    }

    public List<Category> categories() {
        requireAdmin();
        return categoryMapper.findAll();
    }

    public List<Location> locations() {
        requireAdmin();
        return locationMapper.findAll();
    }

    public StatsVO stats() {
        requireAdmin();
        return statsMapper.stats();
    }

    private void requireAdmin() {
        CurrentUser user = UserContext.require();
        if (!"admin".equals(user.role())) {
            throw new BizException(403, "仅管理员可访问");
        }
    }
}
