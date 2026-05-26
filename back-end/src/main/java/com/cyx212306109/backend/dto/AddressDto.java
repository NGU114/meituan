package com.cyx212306109.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AddressDto {

    private AddressDto() {
    }

    public record CreateRequest(
            @NotBlank(message = "联系人不能为空")
            @Size(max = 32, message = "联系人长度不能超过 32")
            String contactName,
            @NotBlank(message = "联系电话不能为空")
            @Pattern(regexp = "^1\\d{10}$", message = "联系电话格式不正确")
            String contactPhone,
            @NotBlank(message = "详细地址不能为空")
            @Size(max = 255, message = "详细地址长度不能超过 255")
            String detailAddress,
            Boolean defaultAddress
    ) {
    }

    public record AddressResponse(Long id, String contactName, String contactPhone, String detailAddress, Boolean defaultAddress) {
    }
}
