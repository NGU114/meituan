package com.cyx212306109.backend.service;

import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.common.BusinessException;
import com.cyx212306109.backend.dto.OrderDto;
import com.cyx212306109.backend.entity.CartItem;
import com.cyx212306109.backend.entity.CustomerOrder;
import com.cyx212306109.backend.entity.OrderComment;
import com.cyx212306109.backend.entity.OrderItem;
import com.cyx212306109.backend.entity.Product;
import com.cyx212306109.backend.entity.Shop;
import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.entity.UserAddress;
import com.cyx212306109.backend.enums.OrderStatus;
import com.cyx212306109.backend.repository.CartItemRepository;
import com.cyx212306109.backend.repository.CustomerOrderRepository;
import com.cyx212306109.backend.repository.OrderCommentRepository;
import com.cyx212306109.backend.repository.OrderItemRepository;
import com.cyx212306109.backend.repository.ProductRepository;
import com.cyx212306109.backend.repository.UserAccountRepository;
import com.cyx212306109.backend.repository.UserAddressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private static final DateTimeFormatter ORDER_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderCommentRepository orderCommentRepository;
    private final CartItemRepository cartItemRepository;
    private final UserAddressRepository userAddressRepository;
    private final ProductRepository productRepository;
    private final UserAccountRepository userAccountRepository;

    public OrderService(CustomerOrderRepository customerOrderRepository,
                        OrderItemRepository orderItemRepository,
                        OrderCommentRepository orderCommentRepository,
                        CartItemRepository cartItemRepository,
                        UserAddressRepository userAddressRepository,
                        ProductRepository productRepository,
                        UserAccountRepository userAccountRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderCommentRepository = orderCommentRepository;
        this.cartItemRepository = cartItemRepository;
        this.userAddressRepository = userAddressRepository;
        this.productRepository = productRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public OrderDto.OrderDetailResponse createOrder(OrderDto.CreateRequest request) {
        Long userId = UserContext.getRequired().id();
        UserAccount user = getUser(userId);
        UserAddress address = userAddressRepository.findByIdAndUserId(request.addressId(), userId)
                .orElseThrow(() -> new BusinessException("地址不存在"));
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByIdAsc(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空，无法下单");
        }

        Shop shop = cartItems.get(0).getProduct().getShop();
        BigDecimal goodsAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException(product.getName() + " 库存不足");
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            goodsAmount = goodsAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        if (goodsAmount.compareTo(shop.getMinOrderAmount()) < 0) {
            throw new BusinessException("未达到起送价 " + shop.getMinOrderAmount());
        }

        CustomerOrder order = new CustomerOrder();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setShop(shop);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setGoodsAmount(goodsAmount);
        order.setDeliveryFee(shop.getDeliveryFee());
        order.setPayAmount(goodsAmount.add(shop.getDeliveryFee()));
        order.setContactName(address.getContactName());
        order.setContactPhone(address.getContactPhone());
        order.setAddressSnapshot(address.getDetailAddress());
        order.setRemark(request.remark());
        customerOrderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setProductPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setAmount(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteByUserId(userId);
        return buildOrderDetail(order);
    }

    @Transactional
    public OrderDto.OrderDetailResponse payOrder(Long orderId) {
        CustomerOrder order = getMyOrder(orderId);
        assertStatus(order, OrderStatus.PENDING_PAYMENT, "只有待支付订单才能支付");
        order.setStatus(OrderStatus.PAID);
        return buildOrderDetail(order);
    }

    @Transactional
    public OrderDto.OrderDetailResponse cancelOrder(Long orderId) {
        CustomerOrder order = getMyOrder(orderId);
        if (!(order.getStatus() == OrderStatus.PENDING_PAYMENT || order.getStatus() == OrderStatus.PAID)) {
            throw new BusinessException("当前订单状态不允许取消");
        }
        restoreStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        return buildOrderDetail(order);
    }

    public List<OrderDto.OrderSummaryResponse> listMyOrders() {
        Long userId = UserContext.getRequired().id();
        return customerOrderRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(this::buildOrderSummary)
                .toList();
    }

    public OrderDto.OrderDetailResponse myOrderDetail(Long orderId) {
        return buildOrderDetail(getMyOrder(orderId));
    }

    @Transactional
    public OrderDto.OrderDetailResponse confirmReceived(Long orderId) {
        CustomerOrder order = getMyOrder(orderId);
        assertStatus(order, OrderStatus.DELIVERING, "只有配送中的订单才能确认收货");
        order.setStatus(OrderStatus.COMPLETED);
        return buildOrderDetail(order);
    }

    @Transactional
    public OrderDto.CommentResponse commentOrder(Long orderId, OrderDto.CommentRequest request) {
        CustomerOrder order = getMyOrder(orderId);
        assertStatus(order, OrderStatus.COMPLETED, "只有已完成订单才能评价");
        if (Boolean.TRUE.equals(order.getCommented())) {
            throw new BusinessException("该订单已评价");
        }

        OrderComment comment = new OrderComment();
        comment.setOrder(order);
        comment.setUser(order.getUser());
        comment.setRating(request.rating());
        comment.setContent(request.content());
        orderCommentRepository.save(comment);
        order.setCommented(true);
        return buildCommentResponse(comment);
    }

    public List<OrderDto.OrderSummaryResponse> listMerchantOrders() {
        Long merchantId = UserContext.getRequired().id();
        return customerOrderRepository.findByShopMerchantIdOrderByIdDesc(merchantId).stream()
                .map(this::buildOrderSummary)
                .toList();
    }

    @Transactional
    public OrderDto.OrderDetailResponse merchantAccept(Long orderId) {
        CustomerOrder order = getMerchantOrder(orderId);
        assertStatus(order, OrderStatus.PAID, "只有已支付订单才能接单");
        order.setStatus(OrderStatus.ACCEPTED);
        return buildOrderDetail(order);
    }

    @Transactional
    public OrderDto.OrderDetailResponse merchantReject(Long orderId) {
        CustomerOrder order = getMerchantOrder(orderId);
        assertStatus(order, OrderStatus.PAID, "只有已支付订单才能拒单");
        restoreStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        return buildOrderDetail(order);
    }

    @Transactional
    public OrderDto.OrderDetailResponse merchantStartPreparing(Long orderId) {
        CustomerOrder order = getMerchantOrder(orderId);
        assertStatus(order, OrderStatus.ACCEPTED, "只有已接单订单才能开始制作");
        order.setStatus(OrderStatus.PREPARING);
        return buildOrderDetail(order);
    }

    @Transactional
    public OrderDto.OrderDetailResponse merchantReady(Long orderId) {
        CustomerOrder order = getMerchantOrder(orderId);
        assertStatus(order, OrderStatus.PREPARING, "只有制作中订单才能标记待配送");
        order.setStatus(OrderStatus.READY_FOR_DELIVERY);
        return buildOrderDetail(order);
    }

    public List<OrderDto.OrderSummaryResponse> listAvailableOrders() {
        return customerOrderRepository.findByStatusOrderByIdAsc(OrderStatus.READY_FOR_DELIVERY).stream()
                .map(this::buildOrderSummary)
                .toList();
    }

    public List<OrderDto.OrderSummaryResponse> listRiderOrders() {
        Long riderId = UserContext.getRequired().id();
        return customerOrderRepository.findByRiderIdOrderByIdDesc(riderId).stream()
                .map(this::buildOrderSummary)
                .toList();
    }

    @Transactional
    public OrderDto.OrderDetailResponse riderTakeOrder(Long orderId) {
        Long riderId = UserContext.getRequired().id();
        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        assertStatus(order, OrderStatus.READY_FOR_DELIVERY, "只有待配送订单才能接单");
        UserAccount rider = getUser(riderId);
        order.setRider(rider);
        order.setStatus(OrderStatus.DELIVERING);
        return buildOrderDetail(order);
    }

    public List<OrderDto.OrderSummaryResponse> listAllOrders() {
        return customerOrderRepository.findAllByOrderByIdDesc().stream()
                .map(this::buildOrderSummary)
                .toList();
    }

    public OrderDto.OrderDetailResponse adminOrderDetail(Long orderId) {
        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        return buildOrderDetail(order);
    }

    private CustomerOrder getMyOrder(Long orderId) {
        Long userId = UserContext.getRequired().id();
        return customerOrderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
    }

    private CustomerOrder getMerchantOrder(Long orderId) {
        Long merchantId = UserContext.getRequired().id();
        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        if (!order.getShop().getMerchant().getId().equals(merchantId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "不能操作其他商家的订单");
        }
        return order;
    }

    private UserAccount getUser(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在"));
    }

    private void restoreStock(CustomerOrder order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }
        orderItemRepository.findByOrderIdOrderByIdAsc(order.getId()).forEach(item -> {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            });
        });
    }

    private void assertStatus(CustomerOrder order, OrderStatus expected, String message) {
        if (order.getStatus() != expected) {
            throw new BusinessException(message);
        }
    }

    private String generateOrderNo() {
        return ORDER_NO_TIME.format(java.time.LocalDateTime.now()) + UUID.randomUUID().toString().substring(0, 6);
    }

    private OrderDto.OrderSummaryResponse buildOrderSummary(CustomerOrder order) {
        return new OrderDto.OrderSummaryResponse(
                order.getId(),
                order.getOrderNo(),
                order.getShop().getId(),
                order.getShop().getName(),
                order.getStatus(),
                order.getStatus().getLabel(),
                order.getPayAmount(),
                order.getRider() == null ? null : order.getRider().getDisplayName(),
                order.getCreatedAt()
        );
    }

    private OrderDto.OrderDetailResponse buildOrderDetail(CustomerOrder order) {
        List<OrderDto.OrderItemResponse> items = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId()).stream()
                .map(item -> new OrderDto.OrderItemResponse(
                        item.getProductName(),
                        item.getProductPrice(),
                        item.getQuantity(),
                        item.getAmount()
                ))
                .toList();

        OrderDto.CommentResponse commentResponse = orderCommentRepository.findByOrderId(order.getId())
                .map(this::buildCommentResponse)
                .orElse(null);

        return new OrderDto.OrderDetailResponse(
                order.getId(),
                order.getOrderNo(),
                order.getShop().getId(),
                order.getShop().getName(),
                order.getShop().getMerchant().getDisplayName(),
                order.getStatus(),
                order.getStatus().getLabel(),
                order.getGoodsAmount(),
                order.getDeliveryFee(),
                order.getPayAmount(),
                order.getContactName(),
                order.getContactPhone(),
                order.getAddressSnapshot(),
                order.getRemark(),
                order.getRider() == null ? null : order.getRider().getDisplayName(),
                order.getCommented(),
                order.getCreatedAt(),
                items,
                commentResponse
        );
    }

    private OrderDto.CommentResponse buildCommentResponse(OrderComment comment) {
        return new OrderDto.CommentResponse(
                comment.getRating(),
                comment.getContent(),
                comment.getUser().getDisplayName(),
                comment.getCreatedAt()
        );
    }
}
