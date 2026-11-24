package com.xunwu.leida.controller;

import com.xunwu.leida.common.ApiResponse;
import com.xunwu.leida.entity.Category;
import com.xunwu.leida.entity.Location;
import com.xunwu.leida.service.BaseDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 基础数据接口，公开访问，返回 { list: [...] }
 */
@RestController
@RequestMapping("/api")
public class BaseDataController {

    private final BaseDataService baseDataService;

    public BaseDataController(BaseDataService baseDataService) {
        this.baseDataService = baseDataService;
    }

    @GetMapping("/categories")
    public ApiResponse<Map<String, List<Category>>> categories() {
        return ApiResponse.ok(Map.of("list", baseDataService.listCategories()));
    }

    @GetMapping("/locations")
    public ApiResponse<Map<String, List<Location>>> locations() {
        return ApiResponse.ok(Map.of("list", baseDataService.listLocations()));
    }
}
