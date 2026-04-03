package com.cyx212306109.backend.repository;

import com.cyx212306109.backend.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    List<Shop> findByOpenTrueOrderByIdAsc();

    List<Shop> findByMerchantIdOrderByIdAsc(Long merchantId);
}
