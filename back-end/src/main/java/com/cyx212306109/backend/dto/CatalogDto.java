package com.cyx212306109.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public final class CatalogDto {

    private CatalogDto() {
    }

    public record ShopSummaryResponse(
            Long id,
            String name,
            String announcement,
            BigDecimal deliveryFee,
            BigDecimal minOrderAmount
    ) {
    }

    public record ProductResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer stock
    ) {
    }

    public record CategoryResponse(
            Long id,
            String name,
            Integer sortOrder,
            List<ProductResponse> products
    ) {
    }

    public record ShopDetailResponse(
            Long id,
            String name,
            String announcement,
            BigDecimal deliveryFee,
            BigDecimal minOrderAmount,
            String merchantName,
            List<CategoryResponse> categories
    ) {
    }
}
