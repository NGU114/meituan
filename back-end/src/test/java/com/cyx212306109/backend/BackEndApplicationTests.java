package com.cyx212306109.backend;

import com.cyx212306109.backend.auth.CurrentUser;
import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.dto.AdminDto;
import com.cyx212306109.backend.dto.AddressDto;
import com.cyx212306109.backend.dto.AuthDto;
import com.cyx212306109.backend.dto.CartDto;
import com.cyx212306109.backend.dto.CatalogDto;
import com.cyx212306109.backend.dto.ManagementDto;
import com.cyx212306109.backend.dto.OrderDto;
import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.repository.ShopRepository;
import com.cyx212306109.backend.repository.UserAccountRepository;
import com.cyx212306109.backend.service.AddressService;
import com.cyx212306109.backend.service.AdminService;
import com.cyx212306109.backend.service.AuthService;
import com.cyx212306109.backend.service.CartService;
import com.cyx212306109.backend.service.CatalogService;
import com.cyx212306109.backend.service.MerchantService;
import com.cyx212306109.backend.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
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
    private MerchantService merchantService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void registerSupportsUserRiderAndMerchantShop() {
        AuthDto.AuthResponse user = authService.register(new AuthDto.RegisterRequest(
                "newuser",
                "123456",
                "新用户",
                "13900000001",
                null,
                null,
                null,
                null,
                null
        ));
        assertThat(user.role()).isEqualTo(RoleType.USER);

        AuthDto.AuthResponse rider = authService.register(new AuthDto.RegisterRequest(
                "newrider",
                "123456",
                "新骑手",
                "13900000002",
                RoleType.RIDER,
                null,
                null,
                null,
                null
        ));
        assertThat(rider.role()).isEqualTo(RoleType.RIDER);

        AuthDto.AuthResponse merchant = authService.register(new AuthDto.RegisterRequest(
                "newmerchant",
                "123456",
                "新商家",
                "13900000003",
                RoleType.MERCHANT,
                "测试新店",
                "新店开业",
                new java.math.BigDecimal("2.50"),
                new java.math.BigDecimal("15.00")
        ));
        assertThat(merchant.role()).isEqualTo(RoleType.MERCHANT);
        assertThat(shopRepository.findByMerchantIdOrderByIdAsc(merchant.userId()))
                .singleElement()
                .satisfies(shop -> {
                    assertThat(shop.getName()).isEqualTo("测试新店");
                    assertThat(shop.getAnnouncement()).isEqualTo("新店开业");
                    assertThat(shop.getDeliveryFee()).isEqualByComparingTo("2.50");
                    assertThat(shop.getMinOrderAmount()).isEqualByComparingTo("15.00");
                });
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
            assertThat(orderService.merchantOrderDetail(orderId).status().name()).isEqualTo("PAID");
            assertThat(orderService.merchantAccept(orderId).status().name()).isEqualTo("ACCEPTED");
            assertThat(orderService.merchantStartPreparing(orderId).status().name()).isEqualTo("PREPARING");
            assertThat(orderService.merchantReady(orderId).status().name()).isEqualTo("READY_FOR_DELIVERY");

            runAs("rider");
            assertThat(orderService.listAvailableOrders()).isNotEmpty();
            assertThat(orderService.riderOrderDetail(orderId).status().name()).isEqualTo("READY_FOR_DELIVERY");
            assertThat(orderService.riderTakeOrder(orderId).status().name()).isEqualTo("DELIVERING");
            OrderDto.OrderDetailResponse deliveredOrder = orderService.riderMarkDelivered(orderId);
            assertThat(deliveredOrder.status().name()).isEqualTo("DELIVERED");
            assertThat(deliveredOrder.timeline()).anySatisfy(step -> {
                assertThat(step.status()).isEqualTo("DELIVERED");
                assertThat(step.current()).isTrue();
                assertThat(step.time()).isNotNull();
            });

            runAs("user");
            OrderDto.OrderDetailResponse completedOrder = orderService.confirmReceived(orderId);
            assertThat(completedOrder.status().name()).isEqualTo("COMPLETED");
            assertThat(completedOrder.timeline()).anySatisfy(step -> {
                assertThat(step.status()).isEqualTo("COMPLETED");
                assertThat(step.current()).isTrue();
                assertThat(step.time()).isNotNull();
            });
            assertThat(orderService.commentOrder(orderId, new OrderDto.CommentRequest(5, "出餐快，口味稳定")).rating()).isEqualTo(5);

            runAs("merchant");
            assertThat(orderService.listMerchantComments())
                    .anySatisfy(comment -> {
                        assertThat(comment.orderId()).isEqualTo(orderId);
                        assertThat(comment.rating()).isEqualTo(5);
                        assertThat(comment.content()).isEqualTo("出餐快，口味稳定");
                        assertThat(comment.userName()).isEqualTo("测试用户");
                    });

            runAs("admin");
            AdminDto.DashboardResponse dashboard = adminService.dashboard();
            assertThat(dashboard.completedOrderCount()).isGreaterThanOrEqualTo(1L);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void refundFlowAndAdminHardDeleteWork() {
        CatalogDto.ShopSummaryResponse shop = catalogService.listOpenShops().get(0);
        CatalogDto.ShopDetailResponse shopDetail = catalogService.shopDetail(shop.id());
        Long productId = shopDetail.categories().get(0).products().get(0).id();

        try {
            runAs("user");
            Long firstOrderId = createPaidOrder(productId);

            runAs("merchant");
            orderService.merchantAccept(firstOrderId);
            orderService.merchantStartPreparing(firstOrderId);
            orderService.merchantReady(firstOrderId);

            runAs("user");
            OrderDto.OrderDetailResponse refundRequest = orderService.requestRefund(firstOrderId);
            assertThat(refundRequest.status().name()).isEqualTo("REFUND_REQUESTED");

            runAs("merchant");
            OrderDto.OrderDetailResponse rejected = orderService.merchantRejectRefund(firstOrderId);
            assertThat(rejected.status().name()).isEqualTo("READY_FOR_DELIVERY");

            runAs("user");
            Long secondOrderId = createPaidOrder(productId);
            assertThat(orderService.requestRefund(secondOrderId).status().name()).isEqualTo("REFUND_REQUESTED");

            runAs("merchant");
            OrderDto.OrderDetailResponse refunded = orderService.merchantApproveRefund(secondOrderId);
            assertThat(refunded.status().name()).isEqualTo("REFUNDED");
            assertThat(refunded.timeline()).anySatisfy(step -> {
                assertThat(step.status()).isEqualTo("REFUNDED");
                assertThat(step.current()).isTrue();
            });

            runAs("admin");
            orderService.adminDeleteOrder(secondOrderId);
            assertThatThrownBy(() -> orderService.adminOrderDetail(secondOrderId))
                    .hasMessageContaining("订单不存在");
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void demoCatalogContainsExpandedMenu() {
        assertThat(catalogService.listOpenShops())
                .extracting(CatalogDto.ShopSummaryResponse::name)
                .contains("川湘小馆", "晨光早餐铺", "城市轻食");

        CatalogDto.ShopSummaryResponse chuanXiang = catalogService.listOpenShops().stream()
                .filter(shop -> shop.name().equals("川湘小馆"))
                .findFirst()
                .orElseThrow();
        CatalogDto.ShopDetailResponse detail = catalogService.shopDetail(chuanXiang.id());

        assertThat(detail.categories())
                .extracting(CatalogDto.CategoryResponse::name)
                .containsExactly("热销", "主食", "小炒", "汤饮");
        assertThat(detail.categories().stream()
                .flatMap(category -> category.products().stream())
                .map(CatalogDto.ProductResponse::name))
                .contains("辣子鸡丁", "水煮鱼片", "鱼香肉丝", "酸梅汤")
                .hasSizeGreaterThanOrEqualTo(18);
    }

    @Test
    void addressUpdateAndDefaultSelectionWorks() {
        try {
            runAs("user");
            AddressDto.AddressResponse created = addressService.create(new AddressDto.CreateRequest(
                    "备用联系人",
                    "13800001000",
                    "上海市徐汇区测试路 18 号",
                    false
            ));

            AddressDto.AddressResponse updated = addressService.update(created.id(), new AddressDto.UpdateRequest(
                    "新联系人",
                    "13800001001",
                    "上海市徐汇区更新路 19 号",
                    true
            ));

            assertThat(updated.defaultAddress()).isTrue();
            assertThat(addressService.listMyAddresses().get(0).id()).isEqualTo(created.id());

            AddressDto.AddressResponse defaultAddress = addressService.makeDefault(addressService.listMyAddresses().get(1).id());
            assertThat(defaultAddress.defaultAddress()).isTrue();
            assertThat(addressService.listMyAddresses().get(0).id()).isEqualTo(defaultAddress.id());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void merchantCanManageCategoriesAndProducts() {
        try {
            runAs("merchant");
            Long shopId = merchantService.listMyShops().get(0).id();

            ManagementDto.CategoryResponse category = merchantService.createCategory(new ManagementDto.CategoryUpsertRequest(
                    shopId,
                    "测试分类",
                    9
            ));
            assertThat(category.name()).isEqualTo("测试分类");

            ManagementDto.CategoryResponse updatedCategory = merchantService.updateCategory(category.id(), new ManagementDto.CategoryUpsertRequest(
                    shopId,
                    "测试新品",
                    10
            ));
            assertThat(updatedCategory.sortOrder()).isEqualTo(10);

            ManagementDto.ProductResponse product = merchantService.createProduct(new ManagementDto.ProductUpsertRequest(
                    shopId,
                    category.id(),
                    "测试菜品",
                    "用于接口测试",
                    new java.math.BigDecimal("9.90"),
                    20,
                    true
            ));
            assertThat(product.enabled()).isTrue();

            ManagementDto.ProductResponse disabled = merchantService.disableProduct(product.id());
            assertThat(disabled.enabled()).isFalse();
            assertThatThrownBy(() -> merchantService.deleteCategory(category.id()))
                    .hasMessageContaining("分类下还有菜品");

            ManagementDto.CategoryResponse emptyCategory = merchantService.createCategory(new ManagementDto.CategoryUpsertRequest(
                    shopId,
                    "空分类",
                    99
            ));
            merchantService.deleteCategory(emptyCategory.id());
            assertThat(merchantService.listMyCategories()).noneMatch(item -> item.id().equals(emptyCategory.id()));
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void adminCanUpdateUserAndShopStatus() {
        try {
            runAs("admin");
            UserAccount user = userAccountRepository.findByUsername("user").orElseThrow();
            ManagementDto.UserResponse disabled = adminService.updateUserStatus(
                    user.getId(),
                    new AdminDto.UserStatusRequest(false)
            );
            assertThat(disabled.enabled()).isFalse();
            assertThatThrownBy(() -> authService.login(new AuthDto.LoginRequest("user", "123456")))
                    .hasMessageContaining("账号已被禁用");

            ManagementDto.UserResponse enabled = adminService.updateUserStatus(
                    user.getId(),
                    new AdminDto.UserStatusRequest(true)
            );
            assertThat(enabled.enabled()).isTrue();

            Long shopId = adminService.listShops().get(0).id();
            ManagementDto.ShopResponse closed = adminService.updateShopStatus(
                    shopId,
                    new AdminDto.ShopStatusRequest(false)
            );
            assertThat(closed.open()).isFalse();

            ManagementDto.ShopResponse opened = adminService.updateShopStatus(
                    shopId,
                    new AdminDto.ShopStatusRequest(true)
            );
            assertThat(opened.open()).isTrue();
        } finally {
            UserContext.clear();
        }
    }

    private void runAs(String username) {
        UserAccount user = userAccountRepository.findByUsername(username).orElseThrow();
        UserContext.set(new CurrentUser(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole()));
    }

    private Long createPaidOrder(Long productId) {
        Long addressId = addressService.listMyAddresses().get(0).id();
        cartService.addItem(new CartDto.AddRequest(productId, 1));
        OrderDto.OrderDetailResponse created = orderService.createOrder(new OrderDto.CreateRequest(addressId, "退款测试"));
        return orderService.payOrder(created.id()).id();
    }

}
