package com.cyx212306109.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public final class CartDto {

    private CartDto() {
    }

    public record AddRequest(
            @NotNull(message = "菜品不能为空")
            Long productId,
            @NotNull(message = "数量不能为空")
            @Min(value = 1, message = "数量至少为 1")
            Integer quantity
    ) {
    }

    public record UpdateRequest(
            @NotNull(message = "数量不能为空")
            @Min(value = 1, message = "数量至少为 1")
            Integer quantity
    ) {
    }

    public record CartItemResponse(
            Long id,
            Long productId,
            String productName,
            Long shopId,
            String shopName,
            BigDecimal price,
            Integer quantity,
            BigDecimal amount
    ) {
    }

    public record CartResponse(Long shopId, String shopName, List<CartItemResponse> items, BigDecimal totalAmount) {
    }
}
