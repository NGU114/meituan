package com.cyx212306109.backend.controller;

import com.cyx212306109.backend.auth.RequireRole;
import com.cyx212306109.backend.common.ApiResponse;
import com.cyx212306109.backend.dto.ManagementDto;
import com.cyx212306109.backend.dto.OrderDto;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.service.MerchantService;
import com.cyx212306109.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
@RequireRole(RoleType.MERCHANT)
public class MerchantController {

    private final OrderService orderService;
    private final MerchantService merchantService;

    public MerchantController(OrderService orderService, MerchantService merchantService) {
        this.orderService = orderService;
        this.merchantService = merchantService;
    }

    @GetMapping("/shops")
    public ApiResponse<List<ManagementDto.ShopResponse>> shops() {
        return ApiResponse.ok(merchantService.listMyShops());
    }

    @PutMapping("/shops/{shopId}")
    public ApiResponse<ManagementDto.ShopResponse> updateShop(@PathVariable Long shopId,
                                                              @Valid @RequestBody ManagementDto.ShopUpdateRequest request) {
        return ApiResponse.ok("店铺更新成功", merchantService.updateShop(shopId, request));
    }

    @GetMapping("/categories")
    public ApiResponse<List<ManagementDto.CategoryResponse>> categories() {
        return ApiResponse.ok(merchantService.listMyCategories());
    }

    @PostMapping("/categories")
    public ApiResponse<ManagementDto.CategoryResponse> createCategory(@Valid @RequestBody ManagementDto.CategoryUpsertRequest request) {
        return ApiResponse.ok("分类新增成功", merchantService.createCategory(request));
    }

    @PutMapping("/categories/{categoryId}")
    public ApiResponse<ManagementDto.CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                                      @Valid @RequestBody ManagementDto.CategoryUpsertRequest request) {
        return ApiResponse.ok("分类更新成功", merchantService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId) {
        merchantService.deleteCategory(categoryId);
        return ApiResponse.ok("分类删除成功");
    }

    @GetMapping("/products")
    public ApiResponse<List<ManagementDto.ProductResponse>> products() {
        return ApiResponse.ok(merchantService.listMyProducts());
    }

    @PostMapping("/products")
    public ApiResponse<ManagementDto.ProductResponse> createProduct(@Valid @RequestBody ManagementDto.ProductUpsertRequest request) {
        return ApiResponse.ok("菜品新增成功", merchantService.createProduct(request));
    }

    @PutMapping("/products/{productId}")
    public ApiResponse<ManagementDto.ProductResponse> updateProduct(@PathVariable Long productId,
                                                                    @Valid @RequestBody ManagementDto.ProductUpsertRequest request) {
        return ApiResponse.ok("菜品更新成功", merchantService.updateProduct(productId, request));
    }

    @DeleteMapping("/products/{productId}")
    public ApiResponse<ManagementDto.ProductResponse> disableProduct(@PathVariable Long productId) {
        return ApiResponse.ok("菜品已下架", merchantService.disableProduct(productId));
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderDto.OrderSummaryResponse>> list() {
        return ApiResponse.ok(orderService.listMerchantOrders());
    }

    @GetMapping("/comments")
    public ApiResponse<List<OrderDto.MerchantCommentResponse>> comments() {
        return ApiResponse.ok(orderService.listMerchantComments());
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<OrderDto.OrderDetailResponse> detail(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.merchantOrderDetail(orderId));
    }

    @PostMapping("/orders/{orderId}/accept")
    public ApiResponse<OrderDto.OrderDetailResponse> accept(@PathVariable Long orderId) {
        return ApiResponse.ok("接单成功", orderService.merchantAccept(orderId));
    }

    @PostMapping("/orders/{orderId}/reject")
    public ApiResponse<OrderDto.OrderDetailResponse> reject(@PathVariable Long orderId) {
        return ApiResponse.ok("拒单成功", orderService.merchantReject(orderId));
    }

    @PostMapping("/orders/{orderId}/preparing")
    public ApiResponse<OrderDto.OrderDetailResponse> preparing(@PathVariable Long orderId) {
        return ApiResponse.ok("订单已进入制作中", orderService.merchantStartPreparing(orderId));
    }

    @PostMapping("/orders/{orderId}/ready")
    public ApiResponse<OrderDto.OrderDetailResponse> ready(@PathVariable Long orderId) {
        return ApiResponse.ok("订单已进入待配送", orderService.merchantReady(orderId));
    }

    @PostMapping("/orders/{orderId}/refund/approve")
    public ApiResponse<OrderDto.OrderDetailResponse> approveRefund(@PathVariable Long orderId) {
        return ApiResponse.ok("退款已同意", orderService.merchantApproveRefund(orderId));
    }

    @PostMapping("/orders/{orderId}/refund/reject")
    public ApiResponse<OrderDto.OrderDetailResponse> rejectRefund(@PathVariable Long orderId) {
        return ApiResponse.ok("退款已拒绝", orderService.merchantRejectRefund(orderId));
    }
}
