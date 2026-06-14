package com.cyx212306109.backend.service;

import com.cyx212306109.backend.auth.CurrentUser;
import com.cyx212306109.backend.auth.JwtService;
import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.common.BusinessException;
import com.cyx212306109.backend.dto.AuthDto;
import com.cyx212306109.backend.entity.Shop;
import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.repository.ShopRepository;
import com.cyx212306109.backend.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository userAccountRepository,
                       ShopRepository shopRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.shopRepository = shopRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        userAccountRepository.findByUsername(request.username()).ifPresent(user -> {
            throw new BusinessException("用户名已存在");
        });

        RoleType role = request.role() == null ? RoleType.USER : request.role();
        if (role == RoleType.ADMIN) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "不支持注册管理员账号");
        }
        if (role == RoleType.MERCHANT) {
            validateMerchantRegister(request);
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user.setPhone(request.phone());
        user.setRole(role);
        user.setEnabled(true);
        userAccountRepository.save(user);

        if (role == RoleType.MERCHANT) {
            createMerchantShop(user, request);
        }

        return buildAuthResponse(user);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        UserAccount user = userAccountRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "账号已被禁用，请联系管理员");
        }

        return buildAuthResponse(user);
    }

    public AuthDto.ProfileResponse currentProfile() {
        CurrentUser currentUser = UserContext.getRequired();
        UserAccount user = userAccountRepository.findById(currentUser.id())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在"));
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "账号已被禁用，请联系管理员");
        }

        return new AuthDto.ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getPhone(),
                user.getRole()
        );
    }

    private AuthDto.AuthResponse buildAuthResponse(UserAccount user) {
        CurrentUser currentUser = new CurrentUser(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
        return new AuthDto.AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole(),
                jwtService.generateToken(currentUser)
        );
    }

    private void validateMerchantRegister(AuthDto.RegisterRequest request) {
        if (request.shopName() == null || request.shopName().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "店铺名称不能为空");
        }
        if (request.shopAnnouncement() == null || request.shopAnnouncement().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "店铺公告不能为空");
        }
        if (request.deliveryFee() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "配送费不能为空");
        }
        if (request.minOrderAmount() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "起送价不能为空");
        }
    }

    private void createMerchantShop(UserAccount merchant, AuthDto.RegisterRequest request) {
        Shop shop = new Shop();
        shop.setMerchant(merchant);
        shop.setName(request.shopName().trim());
        shop.setAnnouncement(request.shopAnnouncement().trim());
        shop.setDeliveryFee(defaultMoney(request.deliveryFee()));
        shop.setMinOrderAmount(defaultMoney(request.minOrderAmount()));
        shop.setOpen(true);
        shopRepository.save(shop);
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
