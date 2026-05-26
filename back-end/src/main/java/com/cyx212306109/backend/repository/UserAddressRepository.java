package com.cyx212306109.backend.repository;

import com.cyx212306109.backend.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUserIdOrderByDefaultAddressDescIdDesc(Long userId);

    Optional<UserAddress> findByIdAndUserId(Long id, Long userId);
}
