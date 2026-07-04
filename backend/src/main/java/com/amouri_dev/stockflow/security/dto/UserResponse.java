package com.amouri_dev.stockflow.security.dto;

import com.amouri_dev.stockflow.common.User;
import com.amouri_dev.stockflow.common.UserRole;
import com.amouri_dev.stockflow.common.UserStatus;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        UserStatus status,
        Set<UserRole> roles
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserStatus(),
                user.getRoles()
        );
    }
}
