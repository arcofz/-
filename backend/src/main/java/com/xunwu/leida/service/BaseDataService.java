package com.xunwu.leida.service;

import com.xunwu.leida.entity.Category;
import com.xunwu.leida.entity.Location;
import com.xunwu.leida.mapper.CategoryMapper;
import com.xunwu.leida.mapper.LocationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 基础数据：分类、地点（公开只读）
 */
@Service
public class BaseDataService {

    private final CategoryMapper categoryMapper;
    private final LocationMapper locationMapper;

    public BaseDataService(CategoryMapper categoryMapper, LocationMapper locationMapper) {
        this.categoryMapper = categoryMapper;
        this.locationMapper = locationMapper;
    }

    public List<Category> listCategories() {
        return categoryMapper.findAll();
    }

    public List<Location> listLocations() {
        return locationMapper.findAll();
    }
}
