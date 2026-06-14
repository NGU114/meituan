package com.cyx212306109.backend.repository;

import com.cyx212306109.backend.entity.OrderComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderCommentRepository extends JpaRepository<OrderComment, Long> {

    Optional<OrderComment> findByOrderId(Long orderId);

    List<OrderComment> findByOrderShopMerchantIdOrderByIdDesc(Long merchantId);

    void deleteByOrderId(Long orderId);
}
