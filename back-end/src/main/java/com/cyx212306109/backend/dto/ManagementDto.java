package com.cyx212306109.backend.dto;

import com.cyx212306109.backend.enums.RoleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public final class ManagementDto {

    private ManagementDto() {
    }

    public record UserResponse(Long id, String username, String displayName, String phone, RoleType role) {
    }

    public record ShopResponse(
            Long id,
            String name,
            String announcement,
            BigDecimal deliveryFee,
            BigDecimal minOrderAmount,
            Boolean open
    ) {
    }

    public record ProductResponse(
            Long id,
            Long shopId,
            String shopName,
            Long categoryId,
            String categoryName,
            String name,
            String description,
            BigDecimal price,
            Integer stock,
            Boolean enabled
    ) {
    }

    public record ShopUpdateRequest(
            @NotBlank(message = "店铺名称不能为空")
            @Size(max = 64, message = "店铺名称不能超过 64")
            String name,
            @NotBlank(message = "公告不能为空")
            @Size(max = 255, message = "公告长度不能超过 255")
            String announcement,
            @NotNull(message = "配送费不能为空")
            @DecimalMin(value = "0.0", message = "配送费不能小于 0")
            BigDecimal deliveryFee,
            @NotNull(message = "起送价不能为空")
            @DecimalMin(value = "0.0", message = "起送价不能小于 0")
            BigDecimal minOrderAmount,
            Boolean open
    ) {
    }

    public record ProductUpsertRequest(
            @NotNull(message = "店铺不能为空")
            Long shopId,
            @NotNull(message = "分类不能为空")
            Long categoryId,
            @NotBlank(message = "菜品名称不能为空")
            @Size(max = 64, message = "菜品名称不能超过 64")
            String name,
            @NotBlank(message = "描述不能为空")
            @Size(max = 255, message = "描述长度不能超过 255")
            String description,
            @NotNull(message = "价格不能为空")
            @DecimalMin(value = "0.0", message = "价格不能小于 0")
            BigDecimal price,
            @NotNull(message = "库存不能为空")
            @Min(value = 0, message = "库存不能小于 0")
            Integer stock,
            Boolean enabled
    ) {
    }
}
