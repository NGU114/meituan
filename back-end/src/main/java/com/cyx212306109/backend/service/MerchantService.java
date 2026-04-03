package com.cyx212306109.backend.service;

import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.common.BusinessException;
import com.cyx212306109.backend.dto.ManagementDto;
import com.cyx212306109.backend.entity.Product;
import com.cyx212306109.backend.entity.ProductCategory;
import com.cyx212306109.backend.entity.Shop;
import com.cyx212306109.backend.repository.ProductCategoryRepository;
import com.cyx212306109.backend.repository.ProductRepository;
import com.cyx212306109.backend.repository.ShopRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MerchantService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public MerchantService(ShopRepository shopRepository,
                           ProductRepository productRepository,
                           ProductCategoryRepository productCategoryRepository) {
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<ManagementDto.ShopResponse> listMyShops() {
        Long merchantId = UserContext.getRequired().id();
        return shopRepository.findByMerchantIdOrderByIdAsc(merchantId).stream()
                .map(this::toShopResponse)
                .toList();
    }

    @Transactional
    public ManagementDto.ShopResponse updateShop(Long shopId, ManagementDto.ShopUpdateRequest request) {
        Shop shop = getMyShop(shopId);
        shop.setName(request.name());
        shop.setAnnouncement(request.announcement());
        shop.setDeliveryFee(request.deliveryFee());
        shop.setMinOrderAmount(request.minOrderAmount());
        shop.setOpen(Boolean.TRUE.equals(request.open()));
        return toShopResponse(shop);
    }

    public List<ManagementDto.ProductResponse> listMyProducts() {
        Long merchantId = UserContext.getRequired().id();
        return productRepository.findByShopMerchantIdOrderByShopIdAscCategoryIdAscIdAsc(merchantId).stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Transactional
    public ManagementDto.ProductResponse createProduct(ManagementDto.ProductUpsertRequest request) {
        Shop shop = getMyShop(request.shopId());
        ProductCategory category = getCategory(shop, request.categoryId());

        Product product = new Product();
        product.setShop(shop);
        product.setCategory(category);
        applyProduct(product, request);
        productRepository.save(product);
        return toProductResponse(product);
    }

    @Transactional
    public ManagementDto.ProductResponse updateProduct(Long productId, ManagementDto.ProductUpsertRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("菜品不存在"));
        if (!product.getShop().getMerchant().getId().equals(UserContext.getRequired().id())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "不能操作其他商家的菜品");
        }

        Shop shop = getMyShop(request.shopId());
        ProductCategory category = getCategory(shop, request.categoryId());
        product.setShop(shop);
        product.setCategory(category);
        applyProduct(product, request);
        return toProductResponse(product);
    }

    private void applyProduct(Product product, ManagementDto.ProductUpsertRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setEnabled(Boolean.TRUE.equals(request.enabled()));
    }

    private Shop getMyShop(Long shopId) {
        Long merchantId = UserContext.getRequired().id();
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new BusinessException("店铺不存在"));
        if (!shop.getMerchant().getId().equals(merchantId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "不能操作其他商家的店铺");
        }
        return shop;
    }

    private ProductCategory getCategory(Shop shop, Long categoryId) {
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        if (!category.getShop().getId().equals(shop.getId())) {
            throw new BusinessException("分类不属于当前店铺");
        }
        return category;
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

    private ManagementDto.ProductResponse toProductResponse(Product product) {
        return new ManagementDto.ProductResponse(
                product.getId(),
                product.getShop().getId(),
                product.getShop().getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getEnabled()
        );
    }
}
