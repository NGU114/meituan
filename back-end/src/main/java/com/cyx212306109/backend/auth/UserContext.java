package com.cyx212306109.backend.auth;

import com.cyx212306109.backend.common.BusinessException;
import org.springframework.http.HttpStatus;

public final class UserContext {

    private static final ThreadLocal<CurrentUser> CURRENT = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(CurrentUser currentUser) {
        CURRENT.set(currentUser);
    }

    public static CurrentUser get() {
        return CURRENT.get();
    }

    public static CurrentUser getRequired() {
        CurrentUser currentUser = CURRENT.get();
        if (currentUser == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        return currentUser;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
