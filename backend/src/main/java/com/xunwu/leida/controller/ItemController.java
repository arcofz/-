package com.xunwu.leida.controller;

import com.xunwu.leida.common.ApiResponse;
import com.xunwu.leida.common.PageResult;
import com.xunwu.leida.dto.ItemCreateRequest;
import com.xunwu.leida.dto.ItemDetailVO;
import com.xunwu.leida.dto.ItemUpdateRequest;
import com.xunwu.leida.dto.ItemVO;
import com.xunwu.leida.dto.MatchVO;
import com.xunwu.leida.service.ItemService;
import com.xunwu.leida.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final MatchService matchService;

    public ItemController(ItemService itemService, MatchService matchService) {
        this.itemService = itemService;
        this.matchService = matchService;
    }

    @GetMapping("/{id}/matches")
    public ApiResponse<List<MatchVO>> matches(@PathVariable long id) {
        return ApiResponse.ok(matchService.matches(id));
    }

    @GetMapping
    public ApiResponse<PageResult<ItemVO>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.ok(itemService.list(type, categoryId, locationId, page, pageSize));
    }

    @PostMapping
    public ApiResponse<ItemDetailVO> create(@Valid @RequestBody ItemCreateRequest req) {
        return ApiResponse.ok(itemService.create(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<ItemDetailVO> detail(@PathVariable long id) {
        return ApiResponse.ok(itemService.detail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<ItemDetailVO> update(@PathVariable long id,
                                            @Valid @RequestBody ItemUpdateRequest req) {
        return ApiResponse.ok(itemService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable long id) {
        itemService.delete(id);
        return ApiResponse.ok(null);
    }
}
