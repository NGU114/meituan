package com.cyx212306109.backend.repository;

import com.cyx212306109.backend.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    List<ProductCategory> findByShopIdOrderBySortOrderAscIdAsc(Long shopId);

    Optional<ProductCategory> findFirstByShopIdAndNameOrderByIdAsc(Long shopId, String name);
}
