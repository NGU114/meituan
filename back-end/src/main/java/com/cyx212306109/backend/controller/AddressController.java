package com.cyx212306109.backend.controller;

import com.cyx212306109.backend.auth.RequireRole;
import com.cyx212306109.backend.common.ApiResponse;
import com.cyx212306109.backend.dto.AddressDto;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequireRole(RoleType.USER)
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ApiResponse<List<AddressDto.AddressResponse>> list() {
        return ApiResponse.ok(addressService.listMyAddresses());
    }

    @PostMapping
    public ApiResponse<AddressDto.AddressResponse> create(@Valid @RequestBody AddressDto.CreateRequest request) {
        return ApiResponse.ok("地址新增成功", addressService.create(request));
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> delete(@PathVariable Long addressId) {
        addressService.delete(addressId);
        return ApiResponse.ok("地址删除成功");
    }
}
