package com.amouri_dev.stockflow.security;

import com.amouri_dev.stockflow.common.User;
import com.amouri_dev.stockflow.common.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByUserStatus(UserStatus status);
}
