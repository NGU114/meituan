package com.cyx212306109.backend.controller;

import com.cyx212306109.backend.auth.RequireRole;
import com.cyx212306109.backend.common.ApiResponse;
import com.cyx212306109.backend.dto.OrderDto;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rider/orders")
@RequireRole(RoleType.RIDER)
public class RiderController {

    private final OrderService orderService;

    public RiderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/available")
    public ApiResponse<List<OrderDto.OrderSummaryResponse>> available() {
        return ApiResponse.ok(orderService.listAvailableOrders());
    }

    @GetMapping("/mine")
    public ApiResponse<List<OrderDto.OrderSummaryResponse>> mine() {
        return ApiResponse.ok(orderService.listRiderOrders());
    }

    @PostMapping("/{orderId}/take")
    public ApiResponse<OrderDto.OrderDetailResponse> take(@PathVariable Long orderId) {
        return ApiResponse.ok("接单成功", orderService.riderTakeOrder(orderId));
    }
}
