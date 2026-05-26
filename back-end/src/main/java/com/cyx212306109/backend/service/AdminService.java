package com.cyx212306109.backend.service;

import com.cyx212306109.backend.dto.AdminDto;
import com.cyx212306109.backend.dto.ManagementDto;
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
                .map(user -> new ManagementDto.UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getDisplayName(),
                        user.getPhone(),
                        user.getRole()
                ))
                .toList();
    }

    public java.util.List<ManagementDto.ShopResponse> listShops() {
        return shopRepository.findAll().stream()
                .map(shop -> new ManagementDto.ShopResponse(
                        shop.getId(),
                        shop.getName(),
                        shop.getAnnouncement(),
                        shop.getDeliveryFee(),
                        shop.getMinOrderAmount(),
                        shop.getOpen()
                ))
                .toList();
    }
}
