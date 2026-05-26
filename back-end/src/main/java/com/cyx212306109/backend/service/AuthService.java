package com.cyx212306109.backend.service;

import com.cyx212306109.backend.auth.CurrentUser;
import com.cyx212306109.backend.auth.JwtService;
import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.common.BusinessException;
import com.cyx212306109.backend.dto.AuthDto;
import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository userAccountRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        userAccountRepository.findByUsername(request.username()).ifPresent(user -> {
            throw new BusinessException("用户名已存在");
        });

        UserAccount user = new UserAccount();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user.setPhone(request.phone());
        user.setRole(RoleType.USER);
        userAccountRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        UserAccount user = userAccountRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        return buildAuthResponse(user);
    }

    public AuthDto.ProfileResponse currentProfile() {
        CurrentUser currentUser = UserContext.getRequired();
        UserAccount user = userAccountRepository.findById(currentUser.id())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在"));

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
}
