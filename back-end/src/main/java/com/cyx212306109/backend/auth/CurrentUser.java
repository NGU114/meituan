package com.cyx212306109.backend.auth;

import com.cyx212306109.backend.enums.RoleType;

public record CurrentUser(Long id, String username, String displayName, RoleType role) {
}
