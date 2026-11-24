package com.xunwu.leida.controller;

import com.xunwu.leida.common.ApiResponse;
import com.xunwu.leida.dto.MatchVO;
import com.xunwu.leida.service.MatchService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/matches/{id}/confirm")
    public ApiResponse<Void> confirm(@PathVariable long id) {
        matchService.confirm(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/matches/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable long id) {
        matchService.reject(id);
        return ApiResponse.ok(null);
    }
}
