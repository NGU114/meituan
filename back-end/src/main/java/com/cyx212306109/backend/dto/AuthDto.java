package com.cyx212306109.backend.dto;

import com.cyx212306109.backend.enums.RoleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public final class AuthDto {

    private AuthDto() {
    }

    public record RegisterRequest(
            @NotBlank(message = "用户名不能为空")
            @Size(min = 4, max = 20, message = "用户名长度需在 4-20 之间")
            String username,
            @NotBlank(message = "密码不能为空")
            @Size(min = 6, max = 20, message = "密码长度需在 6-20 之间")
            String password,
            @NotBlank(message = "昵称不能为空")
            @Size(max = 32, message = "昵称长度不能超过 32")
            String displayName,
            @NotBlank(message = "手机号不能为空")
            @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
            String phone,
            RoleType role,
            @Size(max = 64, message = "店铺名称不能超过 64")
            String shopName,
            @Size(max = 255, message = "店铺公告不能超过 255")
            String shopAnnouncement,
            @DecimalMin(value = "0.0", message = "配送费不能小于 0")
            BigDecimal deliveryFee,
            @DecimalMin(value = "0.0", message = "起送价不能小于 0")
            BigDecimal minOrderAmount
    ) {
    }

    public record LoginRequest(
            @NotBlank(message = "用户名不能为空")
            String username,
            @NotBlank(message = "密码不能为空")
            String password
    ) {
    }

    public record AuthResponse(Long userId, String username, String displayName, RoleType role, String token) {
    }

    public record ProfileResponse(Long userId, String username, String displayName, String phone, RoleType role) {
    }
}
