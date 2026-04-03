package com.cyx212306109.backend.service;

import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.common.BusinessException;
import com.cyx212306109.backend.dto.CartDto;
import com.cyx212306109.backend.entity.CartItem;
import com.cyx212306109.backend.entity.Product;
import com.cyx212306109.backend.entity.Shop;
import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.repository.CartItemRepository;
import com.cyx212306109.backend.repository.ProductRepository;
import com.cyx212306109.backend.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserAccountRepository userAccountRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserAccountRepository userAccountRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public CartDto.CartResponse listMyCart() {
        Long userId = UserContext.getRequired().id();
        return buildCartResponse(cartItemRepository.findByUserIdOrderByIdAsc(userId));
    }

    @Transactional
    public CartDto.CartResponse addItem(CartDto.AddRequest request) {
        Long userId = UserContext.getRequired().id();
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在"));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException("菜品不存在"));
        if (!Boolean.TRUE.equals(product.getEnabled())) {
            throw new BusinessException("菜品已下架");
        }

        List<CartItem> currentItems = cartItemRepository.findByUserIdOrderByIdAsc(userId);
        ensureSameShop(currentItems, product.getShop().getId());

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, product.getId())
                .orElseGet(() -> {
                    CartItem item = new CartItem();
                    item.setUser(user);
                    item.setProduct(product);
                    item.setQuantity(0);
                    return item;
                });

        int newQuantity = cartItem.getQuantity() + request.quantity();
        ensureStock(product, newQuantity);
        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);
        return buildCartResponse(cartItemRepository.findByUserIdOrderByIdAsc(userId));
    }

    @Transactional
    public CartDto.CartResponse updateItem(Long cartItemId, CartDto.UpdateRequest request) {
        Long userId = UserContext.getRequired().id();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException("购物车条目不存在"));
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "不能修改他人的购物车");
        }

        ensureStock(cartItem.getProduct(), request.quantity());
        cartItem.setQuantity(request.quantity());
        return buildCartResponse(cartItemRepository.findByUserIdOrderByIdAsc(userId));
    }

    @Transactional
    public CartDto.CartResponse removeItem(Long cartItemId) {
        Long userId = UserContext.getRequired().id();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException("购物车条目不存在"));
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "不能删除他人的购物车");
        }
        cartItemRepository.delete(cartItem);
        return buildCartResponse(cartItemRepository.findByUserIdOrderByIdAsc(userId));
    }

    @Transactional
    public void clearMine() {
        Long userId = UserContext.getRequired().id();
        cartItemRepository.deleteByUserId(userId);
    }

    private void ensureSameShop(List<CartItem> cartItems, Long shopId) {
        if (!cartItems.isEmpty() && !cartItems.get(0).getProduct().getShop().getId().equals(shopId)) {
            throw new BusinessException("购物车仅支持单店下单，请先清空其他店铺商品");
        }
    }

    private void ensureStock(Product product, Integer quantity) {
        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足，当前剩余 " + product.getStock());
        }
    }

    private CartDto.CartResponse buildCartResponse(List<CartItem> items) {
        if (items.isEmpty()) {
            return new CartDto.CartResponse(null, null, List.of(), BigDecimal.ZERO);
        }

        Shop shop = items.get(0).getProduct().getShop();
        List<CartDto.CartItemResponse> responses = items.stream()
                .map(item -> new CartDto.CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        shop.getId(),
                        shop.getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        BigDecimal totalAmount = responses.stream()
                .map(CartDto.CartItemResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDto.CartResponse(shop.getId(), shop.getName(), responses, totalAmount);
    }
}
