package com.cyx212306109.backend.controller;

import com.cyx212306109.backend.auth.RequireRole;
import com.cyx212306109.backend.common.ApiResponse;
import com.cyx212306109.backend.dto.OrderDto;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequireRole(RoleType.USER)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderDto.OrderDetailResponse> create(@Valid @RequestBody OrderDto.CreateRequest request) {
        return ApiResponse.ok("下单成功", orderService.createOrder(request));
    }

    @PostMapping("/{orderId}/pay")
    public ApiResponse<OrderDto.OrderDetailResponse> pay(@PathVariable Long orderId) {
        return ApiResponse.ok("支付成功", orderService.payOrder(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderDto.OrderDetailResponse> cancel(@PathVariable Long orderId) {
        return ApiResponse.ok("订单已取消", orderService.cancelOrder(orderId));
    }

    @PostMapping("/{orderId}/confirm")
    public ApiResponse<OrderDto.OrderDetailResponse> confirm(@PathVariable Long orderId) {
        return ApiResponse.ok("确认收货成功", orderService.confirmReceived(orderId));
    }

    @PostMapping("/{orderId}/comment")
    public ApiResponse<OrderDto.CommentResponse> comment(@PathVariable Long orderId,
                                                         @Valid @RequestBody OrderDto.CommentRequest request) {
        return ApiResponse.ok("评价成功", orderService.commentOrder(orderId, request));
    }

    @GetMapping
    public ApiResponse<List<OrderDto.OrderSummaryResponse>> list() {
        return ApiResponse.ok(orderService.listMyOrders());
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDto.OrderDetailResponse> detail(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.myOrderDetail(orderId));
    }
}
