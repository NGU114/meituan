package com.cyx212306109.backend.config;

import com.cyx212306109.backend.auth.CurrentUser;
import com.cyx212306109.backend.auth.JwtService;
import com.cyx212306109.backend.auth.RequireRole;
import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.common.BusinessException;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.repository.UserAccountRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final UserAccountRepository userAccountRepository;

    public AuthInterceptor(JwtService jwtService, UserAccountRepository userAccountRepository) {
        this.jwtService = jwtService;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                CurrentUser currentUser = jwtService.parseToken(token);
                boolean enabled = userAccountRepository.findById(currentUser.id())
                        .map(user -> !Boolean.FALSE.equals(user.getEnabled()))
                        .orElse(false);
                if (!enabled) {
                    throw new BusinessException(HttpStatus.FORBIDDEN, "账号已被禁用，请联系管理员");
                }
                UserContext.set(currentUser);
            } catch (JwtException | IllegalArgumentException exception) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED, "登录信息已失效，请重新登录");
            }
        }

        RequireRole requireRole = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequireRole.class);
        if (requireRole == null) {
            requireRole = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RequireRole.class);
        }

        if (requireRole == null) {
            return true;
        }

        CurrentUser currentUser = UserContext.getRequired();
        Set<RoleType> allowedRoles = Arrays.stream(requireRole.value()).collect(Collectors.toSet());
        if (!allowedRoles.contains(currentUser.role())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "当前角色无权访问该接口");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
