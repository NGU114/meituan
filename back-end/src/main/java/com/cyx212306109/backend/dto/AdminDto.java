package com.cyx212306109.backend.dto;

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
}
