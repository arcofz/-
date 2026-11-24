package com.xunwu.leida.controller;

import com.xunwu.leida.common.ApiResponse;
import com.xunwu.leida.dto.HandoverCreateRequest;
import com.xunwu.leida.dto.HandoverVO;
import com.xunwu.leida.service.HandoverService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/handovers")
public class HandoverController {

    private final HandoverService handoverService;

    public HandoverController(HandoverService handoverService) {
        this.handoverService = handoverService;
    }

    @PostMapping
    public ApiResponse<HandoverVO> create(@Valid @RequestBody HandoverCreateRequest req) {
        return ApiResponse.ok(handoverService.create(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<HandoverVO> detail(@PathVariable long id) {
        return ApiResponse.ok(handoverService.detail(id));
    }
}
