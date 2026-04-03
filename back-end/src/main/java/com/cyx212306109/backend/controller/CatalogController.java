package com.cyx212306109.backend.controller;

import com.cyx212306109.backend.common.ApiResponse;
import com.cyx212306109.backend.dto.CatalogDto;
import com.cyx212306109.backend.service.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public ApiResponse<List<CatalogDto.ShopSummaryResponse>> shops() {
        return ApiResponse.ok(catalogService.listOpenShops());
    }

    @GetMapping("/{shopId}")
    public ApiResponse<CatalogDto.ShopDetailResponse> detail(@PathVariable Long shopId) {
        return ApiResponse.ok(catalogService.shopDetail(shopId));
    }
}
