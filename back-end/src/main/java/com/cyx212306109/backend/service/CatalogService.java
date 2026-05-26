package com.cyx212306109.backend.service;

import com.cyx212306109.backend.common.BusinessException;
import com.cyx212306109.backend.dto.CatalogDto;
import com.cyx212306109.backend.entity.Product;
import com.cyx212306109.backend.entity.ProductCategory;
import com.cyx212306109.backend.entity.Shop;
import com.cyx212306109.backend.repository.ProductCategoryRepository;
import com.cyx212306109.backend.repository.ProductRepository;
import com.cyx212306109.backend.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CatalogService {

    private final ShopRepository shopRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    public CatalogService(ShopRepository shopRepository,
                          ProductCategoryRepository productCategoryRepository,
                          ProductRepository productRepository) {
        this.shopRepository = shopRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
    }

    public List<CatalogDto.ShopSummaryResponse> listOpenShops() {
        return shopRepository.findByOpenTrueOrderByIdAsc().stream()
                .map(shop -> new CatalogDto.ShopSummaryResponse(
                        shop.getId(),
                        shop.getName(),
                        shop.getAnnouncement(),
                        shop.getDeliveryFee(),
                        shop.getMinOrderAmount()
                ))
                .toList();
    }

    public CatalogDto.ShopDetailResponse shopDetail(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new BusinessException("店铺不存在"));

        List<ProductCategory> categories = productCategoryRepository.findByShopIdOrderBySortOrderAscIdAsc(shopId);
        Map<Long, List<Product>> productMap = productRepository.findByShopIdAndEnabledTrueOrderByCategoryIdAscIdAsc(shopId).stream()
                .collect(Collectors.groupingBy(product -> product.getCategory().getId()));

        List<CatalogDto.CategoryResponse> categoryResponses = categories.stream()
                .map(category -> new CatalogDto.CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getSortOrder(),
                        productMap.getOrDefault(category.getId(), List.of()).stream()
                                .map(this::toProductResponse)
                                .toList()
                ))
                .toList();

        return new CatalogDto.ShopDetailResponse(
                shop.getId(),
                shop.getName(),
                shop.getAnnouncement(),
                shop.getDeliveryFee(),
                shop.getMinOrderAmount(),
                shop.getMerchant().getDisplayName(),
                categoryResponses
        );
    }

    private CatalogDto.ProductResponse toProductResponse(Product product) {
        return new CatalogDto.ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }
}
