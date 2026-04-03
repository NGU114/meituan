package com.cyx212306109.backend.repository;

import com.cyx212306109.backend.entity.CustomerOrder;
import com.cyx212306109.backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    Optional<CustomerOrder> findByIdAndUserId(Long id, Long userId);

    List<CustomerOrder> findByUserIdOrderByIdDesc(Long userId);

    List<CustomerOrder> findByShopMerchantIdOrderByIdDesc(Long merchantId);

    List<CustomerOrder> findByRiderIdOrderByIdDesc(Long riderId);

    List<CustomerOrder> findByStatusOrderByIdAsc(OrderStatus status);

    List<CustomerOrder> findAllByOrderByIdDesc();
}
