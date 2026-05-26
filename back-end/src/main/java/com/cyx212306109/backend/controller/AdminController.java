package com.cyx212306109.backend.controller;

import com.cyx212306109.backend.auth.RequireRole;
import com.cyx212306109.backend.common.ApiResponse;
import com.cyx212306109.backend.dto.AdminDto;
import com.cyx212306109.backend.dto.ManagementDto;
import com.cyx212306109.backend.dto.OrderDto;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.service.AdminService;
import com.cyx212306109.backend.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequireRole(RoleType.ADMIN)
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;

    public AdminController(AdminService adminService, OrderService orderService) {
        this.adminService = adminService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<AdminDto.DashboardResponse> dashboard() {
        return ApiResponse.ok(adminService.dashboard());
    }

    @GetMapping("/users")
    public ApiResponse<List<ManagementDto.UserResponse>> users() {
        return ApiResponse.ok(adminService.listUsers());
    }

    @GetMapping("/shops")
    public ApiResponse<List<ManagementDto.ShopResponse>> shops() {
        return ApiResponse.ok(adminService.listShops());
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderDto.OrderSummaryResponse>> orders() {
        return ApiResponse.ok(orderService.listAllOrders());
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<OrderDto.OrderDetailResponse> orderDetail(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.adminOrderDetail(orderId));
    }
}
