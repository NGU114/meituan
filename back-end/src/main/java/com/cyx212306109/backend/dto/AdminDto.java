package com.cyx212306109.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public final class AdminDto {

    private AdminDto() {
    }

    public record DashboardResponse(
            long userCount,
            long merchantCount,
            long riderCount,
            long shopCount,
            long orderCount,
            long completedOrderCount,
            BigDecimal totalRevenue
    ) {
    }

    public record UserStatusRequest(
            @NotNull(message = "用户状态不能为空")
            Boolean enabled
    ) {
    }

    public record ShopStatusRequest(
            @NotNull(message = "店铺状态不能为空")
            Boolean open
    ) {
    }
}
