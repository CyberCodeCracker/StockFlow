package com.amouri_dev.stockflow.security.dto;

import com.amouri_dev.stockflow.common.UserRole;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record ApproveUserRequest(
        @NotEmpty Set<UserRole> roles
) {
}
