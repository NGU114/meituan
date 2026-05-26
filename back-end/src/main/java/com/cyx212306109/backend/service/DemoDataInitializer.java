package com.cyx212306109.backend.service;

import com.cyx212306109.backend.entity.Product;
import com.cyx212306109.backend.entity.ProductCategory;
import com.cyx212306109.backend.entity.Shop;
import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.entity.UserAddress;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.repository.ProductCategoryRepository;
import com.cyx212306109.backend.repository.ProductRepository;
import com.cyx212306109.backend.repository.ShopRepository;
import com.cyx212306109.backend.repository.UserAccountRepository;
import com.cyx212306109.backend.repository.UserAddressRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final UserAddressRepository userAddressRepository;
    private final ShopRepository shopRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(UserAccountRepository userAccountRepository,
                               UserAddressRepository userAddressRepository,
                               ShopRepository shopRepository,
                               ProductCategoryRepository productCategoryRepository,
                               ProductRepository productRepository,
                               PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.userAddressRepository = userAddressRepository;
        this.shopRepository = shopRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userAccountRepository.count() > 0) {
            return;
        }

        UserAccount admin = createUser("admin", "123456", "平台管理员", "13800000001", RoleType.ADMIN);
        UserAccount merchant = createUser("merchant", "123456", "川湘小馆", "13800000002", RoleType.MERCHANT);
        UserAccount rider = createUser("rider", "123456", "极速骑手", "13800000003", RoleType.RIDER);
        UserAccount user = createUser("user", "123456", "测试用户", "13800000004", RoleType.USER);

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setContactName("测试用户");
        address.setContactPhone("13800000004");
        address.setDetailAddress("上海市浦东新区软件园 100 号");
        address.setDefaultAddress(true);
        userAddressRepository.save(address);

        Shop shop = new Shop();
        shop.setMerchant(merchant);
        shop.setName("川湘小馆");
        shop.setAnnouncement("现炒现做，30 分钟送达");
        shop.setDeliveryFee(new BigDecimal("4.00"));
        shop.setMinOrderAmount(new BigDecimal("20.00"));
        shop.setOpen(true);
        shopRepository.save(shop);

        ProductCategory hot = new ProductCategory();
        hot.setShop(shop);
        hot.setName("热销");
        hot.setSortOrder(1);
        productCategoryRepository.save(hot);

        ProductCategory staple = new ProductCategory();
        staple.setShop(shop);
        staple.setName("主食");
        staple.setSortOrder(2);
        productCategoryRepository.save(staple);

        createProduct(shop, hot, "毛血旺", "招牌川味，下饭必点", "36.00", 50);
        createProduct(shop, hot, "小炒黄牛肉", "鲜嫩牛肉，微辣", "32.00", 50);
        createProduct(shop, staple, "扬州炒饭", "粒粒分明，份量足", "18.00", 80);
        createProduct(shop, staple, "酸辣粉", "酸辣开胃", "16.00", 80);
    }

    private UserAccount createUser(String username, String password, String displayName, String phone, RoleType role) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setPhone(phone);
        user.setRole(role);
        return userAccountRepository.save(user);
    }

    private void createProduct(Shop shop, ProductCategory category, String name, String description, String price, int stock) {
        Product product = new Product();
        product.setShop(shop);
        product.setCategory(category);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setStock(stock);
        product.setEnabled(true);
        productRepository.save(product);
    }
}
