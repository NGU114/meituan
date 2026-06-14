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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
    @Transactional
    public void run(String... args) {
        ensureUser("admin", "123456", "平台管理员", "13800000001", RoleType.ADMIN);
        UserAccount merchant = ensureUser("merchant", "123456", "川湘小馆", "13800000002", RoleType.MERCHANT);
        ensureUser("rider", "123456", "极速骑手", "13800000003", RoleType.RIDER);
        UserAccount user = ensureUser("user", "123456", "测试用户", "13800000004", RoleType.USER);

        ensureDefaultAddress(user);
        seedChuanXiangShop(merchant);
        seedBreakfastShop(merchant);
        seedLightFoodShop(merchant);
    }

    private void seedChuanXiangShop(UserAccount merchant) {
        Shop shop = ensureShop(merchant, "川湘小馆", "现炒现做，30 分钟送达", "4.00", "20.00");

        ProductCategory hot = ensureCategory(shop, "热销", 1);
        ProductCategory staple = ensureCategory(shop, "主食", 2);
        ProductCategory stirFry = ensureCategory(shop, "小炒", 3);
        ProductCategory soupDrink = ensureCategory(shop, "汤饮", 4);

        ensureProduct(shop, hot, "毛血旺", "招牌川味，下饭必点", "36.00", 50);
        ensureProduct(shop, hot, "小炒黄牛肉", "鲜嫩牛肉，微辣", "32.00", 50);
        ensureProduct(shop, hot, "辣子鸡丁", "干香麻辣，鸡丁外酥里嫩", "34.00", 60);
        ensureProduct(shop, hot, "水煮鱼片", "现切鱼片，麻辣鲜香", "42.00", 45);
        ensureProduct(shop, hot, "干锅花菜", "锅气足，香辣脆嫩", "24.00", 70);
        ensureProduct(shop, hot, "麻婆豆腐", "麻辣浓香，拌饭很合适", "18.00", 80);

        ensureProduct(shop, staple, "扬州炒饭", "粒粒分明，份量足", "18.00", 80);
        ensureProduct(shop, staple, "酸辣粉", "酸辣开胃", "16.00", 80);
        ensureProduct(shop, staple, "米饭", "东北大米，单人份", "3.00", 200);
        ensureProduct(shop, staple, "牛肉炒河粉", "河粉爽滑，牛肉鲜嫩", "22.00", 75);
        ensureProduct(shop, staple, "担担面", "芝麻酱香，微麻微辣", "19.00", 70);

        ensureProduct(shop, stirFry, "鱼香肉丝", "酸甜咸鲜，经典川味", "26.00", 60);
        ensureProduct(shop, stirFry, "回锅肉", "蒜苗爆香，肥瘦相间", "29.00", 55);
        ensureProduct(shop, stirFry, "青椒肉丝", "清爽下饭，微辣", "24.00", 65);
        ensureProduct(shop, stirFry, "番茄炒蛋", "酸甜开胃，家常口味", "16.00", 90);

        ensureProduct(shop, soupDrink, "紫菜蛋花汤", "清淡暖胃，现煮出餐", "12.00", 90);
        ensureProduct(shop, soupDrink, "冰粉", "红糖冰粉，解辣清爽", "9.00", 100);
        ensureProduct(shop, soupDrink, "酸梅汤", "冰镇酸梅汤，酸甜解腻", "8.00", 120);
    }

    private void seedBreakfastShop(UserAccount merchant) {
        Shop breakfastShop = ensureShop(merchant, "晨光早餐铺", "热粥包点，早晚都营业", "3.00", "12.00");

        ProductCategory breakfast = ensureCategory(breakfastShop, "早餐套餐", 1);
        ProductCategory drinks = ensureCategory(breakfastShop, "饮品", 2);

        ensureProduct(breakfastShop, breakfast, "鲜肉小笼包", "一笼 6 个，汤汁饱满", "12.00", 100);
        ensureProduct(breakfastShop, breakfast, "皮蛋瘦肉粥", "慢熬米粥，口感绵密", "10.00", 100);
        ensureProduct(breakfastShop, breakfast, "鸡蛋灌饼", "现烙饼皮，夹蛋加酱", "9.00", 120);
        ensureProduct(breakfastShop, breakfast, "葱油拌面", "葱香浓郁，早午都合适", "11.00", 90);
        ensureProduct(breakfastShop, breakfast, "油条豆浆套餐", "一根油条配热豆浆", "8.00", 120);
        ensureProduct(breakfastShop, drinks, "现磨豆浆", "微甜热饮", "5.00", 120);
        ensureProduct(breakfastShop, drinks, "冰美式", "清爽少负担", "12.00", 80);
        ensureProduct(breakfastShop, drinks, "热牛奶", "温热鲜奶，口感顺滑", "7.00", 100);
    }

    private void seedLightFoodShop(UserAccount merchant) {
        Shop lightFoodShop = ensureShop(merchant, "城市轻食", "低脂轻负担，工作日常备", "5.00", "25.00");

        ProductCategory salad = ensureCategory(lightFoodShop, "沙拉轻食", 1);
        ProductCategory dessertDrink = ensureCategory(lightFoodShop, "饮品甜点", 2);

        ensureProduct(lightFoodShop, salad, "鸡胸肉能量碗", "鸡胸肉、藜麦、时蔬组合", "29.00", 60);
        ensureProduct(lightFoodShop, salad, "牛油果虾仁沙拉", "虾仁弹嫩，牛油果绵密", "36.00", 45);
        ensureProduct(lightFoodShop, salad, "金枪鱼三明治", "全麦吐司，清爽饱腹", "24.00", 70);
        ensureProduct(lightFoodShop, salad, "藜麦时蔬沙拉", "多种时蔬，油醋汁调味", "26.00", 65);
        ensureProduct(lightFoodShop, dessertDrink, "低脂酸奶杯", "酸奶搭配燕麦和水果", "16.00", 80);
        ensureProduct(lightFoodShop, dessertDrink, "柠檬气泡水", "冰爽气泡，少糖配方", "10.00", 100);
    }

    private UserAccount ensureUser(String username, String password, String displayName, String phone, RoleType role) {
        UserAccount user = userAccountRepository.findByUsername(username)
                .orElseGet(UserAccount::new);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setPhone(phone);
        user.setRole(role);
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }
        return userAccountRepository.save(user);
    }

    private void ensureDefaultAddress(UserAccount user) {
        List<UserAddress> addresses = userAddressRepository.findByUserIdOrderByDefaultAddressDescIdDesc(user.getId());
        if (!addresses.isEmpty()) {
            return;
        }
        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setContactName("测试用户");
        address.setContactPhone("13800000004");
        address.setDetailAddress("上海市浦东新区软件园 100 号");
        address.setDefaultAddress(true);
        userAddressRepository.save(address);
    }

    private Shop ensureShop(UserAccount merchant, String name, String announcement, String deliveryFee, String minOrderAmount) {
        Shop shop = shopRepository.findFirstByNameOrderByIdAsc(name)
                .orElseGet(Shop::new);
        shop.setMerchant(merchant);
        shop.setName(name);
        shop.setAnnouncement(announcement);
        shop.setDeliveryFee(new BigDecimal(deliveryFee));
        shop.setMinOrderAmount(new BigDecimal(minOrderAmount));
        if (shop.getOpen() == null) {
            shop.setOpen(true);
        }
        return shopRepository.save(shop);
    }

    private ProductCategory ensureCategory(Shop shop, String name, int sortOrder) {
        ProductCategory category = productCategoryRepository.findFirstByShopIdAndNameOrderByIdAsc(shop.getId(), name)
                .orElseGet(ProductCategory::new);
        category.setShop(shop);
        category.setName(name);
        category.setSortOrder(sortOrder);
        return productCategoryRepository.save(category);
    }

    private void ensureProduct(Shop shop, ProductCategory category, String name, String description, String price, int stock) {
        Product product = productRepository.findFirstByShopIdAndNameOrderByIdAsc(shop.getId(), name)
                .orElseGet(Product::new);
        product.setShop(shop);
        product.setCategory(category);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setStock(product.getStock() == null ? stock : Math.max(product.getStock(), stock));
        product.setEnabled(true);
        productRepository.save(product);
    }
}
