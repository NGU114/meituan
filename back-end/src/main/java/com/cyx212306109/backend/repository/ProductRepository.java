package com.cyx212306109.backend.repository;

import com.cyx212306109.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShopIdAndEnabledTrueOrderByCategoryIdAscIdAsc(Long shopId);

    List<Product> findByShopMerchantIdOrderByShopIdAscCategoryIdAscIdAsc(Long merchantId);

    Optional<Product> findFirstByShopIdAndNameOrderByIdAsc(Long shopId, String name);

    boolean existsByCategoryId(Long categoryId);
}
