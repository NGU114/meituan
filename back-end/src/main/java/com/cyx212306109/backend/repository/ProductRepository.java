package com.cyx212306109.backend.repository;

import com.cyx212306109.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShopIdAndEnabledTrueOrderByCategoryIdAscIdAsc(Long shopId);

    List<Product> findByShopMerchantIdOrderByShopIdAscCategoryIdAscIdAsc(Long merchantId);
}
