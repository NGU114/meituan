package com.cyx212306109.backend;

import com.cyx212306109.backend.auth.CurrentUser;
import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.dto.AdminDto;
import com.cyx212306109.backend.dto.AuthDto;
import com.cyx212306109.backend.dto.CartDto;
import com.cyx212306109.backend.dto.CatalogDto;
import com.cyx212306109.backend.dto.OrderDto;
import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.repository.UserAccountRepository;
import com.cyx212306109.backend.service.AddressService;
import com.cyx212306109.backend.service.AdminService;
import com.cyx212306109.backend.service.AuthService;
import com.cyx212306109.backend.service.CartService;
import com.cyx212306109.backend.service.CatalogService;
import com.cyx212306109.backend.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BackEndApplicationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void fullOrderFlowWorks() throws Exception {
        AuthDto.AuthResponse userLogin = authService.login(new AuthDto.LoginRequest("user", "123456"));
        assertThat(userLogin.token()).isNotBlank();

        CatalogDto.ShopSummaryResponse shop = catalogService.listOpenShops().get(0);
        CatalogDto.ShopDetailResponse shopDetail = catalogService.shopDetail(shop.id());
        Long productId = shopDetail.categories().get(0).products().get(0).id();

        try {
            runAs("user");
            Long addressId = addressService.listMyAddresses().get(0).id();
            CartDto.CartResponse cart = cartService.addItem(new CartDto.AddRequest(productId, 1));
            assertThat(cart.totalAmount()).isPositive();

            OrderDto.OrderDetailResponse createdOrder = orderService.createOrder(new OrderDto.CreateRequest(addressId, "少辣"));
            Long orderId = createdOrder.id();
            assertThat(createdOrder.status().name()).isEqualTo("PENDING_PAYMENT");

            OrderDto.OrderDetailResponse paidOrder = orderService.payOrder(orderId);
            assertThat(paidOrder.status().name()).isEqualTo("PAID");

            runAs("merchant");
            assertThat(orderService.merchantAccept(orderId).status().name()).isEqualTo("ACCEPTED");
            assertThat(orderService.merchantStartPreparing(orderId).status().name()).isEqualTo("PREPARING");
            assertThat(orderService.merchantReady(orderId).status().name()).isEqualTo("READY_FOR_DELIVERY");

            runAs("rider");
            assertThat(orderService.listAvailableOrders()).isNotEmpty();
            assertThat(orderService.riderTakeOrder(orderId).status().name()).isEqualTo("DELIVERING");

            runAs("user");
            assertThat(orderService.confirmReceived(orderId).status().name()).isEqualTo("COMPLETED");
            assertThat(orderService.commentOrder(orderId, new OrderDto.CommentRequest(5, "出餐快，口味稳定")).rating()).isEqualTo(5);

            runAs("admin");
            AdminDto.DashboardResponse dashboard = adminService.dashboard();
            assertThat(dashboard.completedOrderCount()).isGreaterThanOrEqualTo(1L);
        } finally {
            UserContext.clear();
        }
    }

    private void runAs(String username) {
        UserAccount user = userAccountRepository.findByUsername(username).orElseThrow();
        UserContext.set(new CurrentUser(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole()));
    }

}
