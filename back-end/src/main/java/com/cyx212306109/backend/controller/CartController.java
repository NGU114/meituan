package com.cyx212306109.backend.controller;

import com.cyx212306109.backend.auth.RequireRole;
import com.cyx212306109.backend.common.ApiResponse;
import com.cyx212306109.backend.dto.CartDto;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequireRole(RoleType.USER)
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartDto.CartResponse> myCart() {
        return ApiResponse.ok(cartService.listMyCart());
    }

    @PostMapping("/items")
    public ApiResponse<CartDto.CartResponse> addItem(@Valid @RequestBody CartDto.AddRequest request) {
        return ApiResponse.ok("加入购物车成功", cartService.addItem(request));
    }

    @PutMapping("/items/{cartItemId}")
    public ApiResponse<CartDto.CartResponse> updateItem(@PathVariable Long cartItemId,
                                                        @Valid @RequestBody CartDto.UpdateRequest request) {
        return ApiResponse.ok("购物车更新成功", cartService.updateItem(cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<CartDto.CartResponse> removeItem(@PathVariable Long cartItemId) {
        return ApiResponse.ok("购物车删除成功", cartService.removeItem(cartItemId));
    }

    @DeleteMapping
    public ApiResponse<Void> clear() {
        cartService.clearMine();
        return ApiResponse.ok("购物车已清空");
    }
}
