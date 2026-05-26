package com.cyx212306109.backend.dto;

import com.cyx212306109.backend.enums.OrderStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class OrderDto {

    private OrderDto() {
    }

    public record CreateRequest(
            @NotNull(message = "地址不能为空")
            Long addressId,
            @Size(max = 255, message = "备注长度不能超过 255")
            String remark
    ) {
    }

    public record CommentRequest(
            @NotNull(message = "评分不能为空")
            @Min(value = 1, message = "评分不能低于 1")
            @Max(value = 5, message = "评分不能高于 5")
            Integer rating,
            @NotBlank(message = "评价内容不能为空")
            @Size(max = 255, message = "评价长度不能超过 255")
            String content
    ) {
    }

    public record OrderItemResponse(String productName, BigDecimal productPrice, Integer quantity, BigDecimal amount) {
    }

    public record CommentResponse(Integer rating, String content, String userName, LocalDateTime createdAt) {
    }

    public record OrderSummaryResponse(
            Long id,
            String orderNo,
            Long shopId,
            String shopName,
            OrderStatus status,
            String statusLabel,
            BigDecimal payAmount,
            String riderName,
            LocalDateTime createdAt
    ) {
    }

    public record OrderDetailResponse(
            Long id,
            String orderNo,
            Long shopId,
            String shopName,
            String merchantName,
            OrderStatus status,
            String statusLabel,
            BigDecimal goodsAmount,
            BigDecimal deliveryFee,
            BigDecimal payAmount,
            String contactName,
            String contactPhone,
            String addressSnapshot,
            String remark,
            String riderName,
            Boolean commented,
            LocalDateTime createdAt,
            List<OrderItemResponse> items,
            CommentResponse comment
    ) {
    }
}
