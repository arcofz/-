package com.xunwu.leida.controller;

import com.xunwu.leida.common.ApiResponse;
import com.xunwu.leida.common.PageResult;
import com.xunwu.leida.dto.ClaimApproveRequest;
import com.xunwu.leida.dto.ClaimApproveVO;
import com.xunwu.leida.dto.ClaimCreateRequest;
import com.xunwu.leida.dto.ClaimRejectRequest;
import com.xunwu.leida.dto.ClaimVO;
import com.xunwu.leida.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ApiResponse<ClaimVO> create(@Valid @RequestBody ClaimCreateRequest req) {
        return ApiResponse.ok(claimService.create(req));
    }

    @GetMapping("/mine")
    public ApiResponse<PageResult<ClaimVO>> mine(
            @RequestParam(defaultValue = "all") String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.ok(claimService.mine(role, status, page, pageSize));
    }

    /** body 可选;两个字段都传或都不传 */
    @PostMapping("/{id}/approve")
    public ApiResponse<ClaimApproveVO> approve(@PathVariable long id,
                                               @Valid @RequestBody(required = false) ClaimApproveRequest req) {
        return ApiResponse.ok(claimService.approve(id, req));
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<ClaimVO> reject(@PathVariable long id,
                                       @Valid @RequestBody ClaimRejectRequest req) {
        return ApiResponse.ok(claimService.reject(id, req));
    }
}
