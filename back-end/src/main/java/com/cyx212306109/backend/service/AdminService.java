package com.cyx212306109.backend.service;

import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.common.BusinessException;
import com.cyx212306109.backend.dto.AdminDto;
import com.cyx212306109.backend.dto.ManagementDto;
import com.cyx212306109.backend.entity.Shop;
import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.enums.OrderStatus;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.repository.CustomerOrderRepository;
import com.cyx212306109.backend.repository.ShopRepository;
import com.cyx212306109.backend.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final UserAccountRepository userAccountRepository;
    private final ShopRepository shopRepository;
    private final CustomerOrderRepository customerOrderRepository;

    public AdminService(UserAccountRepository userAccountRepository,
                        ShopRepository shopRepository,
                        CustomerOrderRepository customerOrderRepository) {
        this.userAccountRepository = userAccountRepository;
        this.shopRepository = shopRepository;
        this.customerOrderRepository = customerOrderRepository;
    }

    public AdminDto.DashboardResponse dashboard() {
        long completedOrderCount = customerOrderRepository.findByStatusOrderByIdAsc(OrderStatus.COMPLETED).size();
        BigDecimal totalRevenue = customerOrderRepository.findAllByOrderByIdDesc().stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .map(order -> order.getPayAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminDto.DashboardResponse(
                userAccountRepository.findByRole(RoleType.USER).size(),
                userAccountRepository.findByRole(RoleType.MERCHANT).size(),
                userAccountRepository.findByRole(RoleType.RIDER).size(),
                shopRepository.count(),
                customerOrderRepository.count(),
                completedOrderCount,
                totalRevenue
        );
    }

    public java.util.List<ManagementDto.UserResponse> listUsers() {
        return userAccountRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    public java.util.List<ManagementDto.ShopResponse> listShops() {
        return shopRepository.findAll().stream()
                .map(this::toShopResponse)
                .toList();
    }

    @Transactional
    public ManagementDto.UserResponse updateUserStatus(Long userId, AdminDto.UserStatusRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (user.getId().equals(UserContext.getRequired().id())) {
            throw new BusinessException("不能禁用当前登录的管理员账号");
        }
        if (user.getRole() == RoleType.ADMIN && !Boolean.TRUE.equals(request.enabled())) {
            throw new BusinessException("管理员账号不能被禁用");
        }
        user.setEnabled(Boolean.TRUE.equals(request.enabled()));
        return toUserResponse(user);
    }

    @Transactional
    public ManagementDto.ShopResponse updateShopStatus(Long shopId, AdminDto.ShopStatusRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new BusinessException("店铺不存在"));
        shop.setOpen(Boolean.TRUE.equals(request.open()));
        return toShopResponse(shop);
    }

    private ManagementDto.UserResponse toUserResponse(UserAccount user) {
        return new ManagementDto.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getPhone(),
                user.getRole(),
                !Boolean.FALSE.equals(user.getEnabled())
        );
    }

    private ManagementDto.ShopResponse toShopResponse(Shop shop) {
        return new ManagementDto.ShopResponse(
                shop.getId(),
                shop.getName(),
                shop.getAnnouncement(),
                shop.getDeliveryFee(),
                shop.getMinOrderAmount(),
                shop.getOpen()
        );
    }
}
