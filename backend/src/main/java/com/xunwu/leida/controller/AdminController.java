package com.xunwu.leida.controller;

import com.xunwu.leida.common.ApiResponse;
import com.xunwu.leida.common.PageResult;
import com.xunwu.leida.dto.StatsVO;
import com.xunwu.leida.dto.UserVO;
import com.xunwu.leida.entity.Category;
import com.xunwu.leida.entity.Location;
import com.xunwu.leida.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 后台管理接口,权限在 AdminService 统一校验
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ApiResponse<PageResult<UserVO>> users(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.ok(adminService.users(keyword, page, pageSize));
    }

    @GetMapping("/categories")
    public ApiResponse<Map<String, List<Category>>> categories() {
        return ApiResponse.ok(Map.of("list", adminService.categories()));
    }

    @GetMapping("/locations")
    public ApiResponse<Map<String, List<Location>>> locations() {
        return ApiResponse.ok(Map.of("list", adminService.locations()));
    }

    @GetMapping("/stats")
    public ApiResponse<StatsVO> stats() {
        return ApiResponse.ok(adminService.stats());
    }
}
