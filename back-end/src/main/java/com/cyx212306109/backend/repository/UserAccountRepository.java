package com.cyx212306109.backend.repository;

import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    List<UserAccount> findByRole(RoleType role);
}
