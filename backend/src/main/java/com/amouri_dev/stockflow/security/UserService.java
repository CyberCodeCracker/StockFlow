package com.amouri_dev.stockflow.security;

import com.amouri_dev.stockflow.common.User;
import com.amouri_dev.stockflow.common.UserStatus;
import com.amouri_dev.stockflow.security.dto.ApproveUserRequest;
import com.amouri_dev.stockflow.security.dto.RegisterRequest;
import com.amouri_dev.stockflow.security.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) {
        if (this.userRepository.existsByEmail(registerRequest.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        // Self-registered users start PENDING and disabled, with no roles,
        // until an admin approves and assigns their role(s).
        User user = new User();
        user.setFirstName(registerRequest.firstName());
        user.setLastName(registerRequest.lastName());
        user.setEmail(registerRequest.email());
        user.setPassword(this.passwordEncoder.encode(registerRequest.password()));
        user.setUserStatus(UserStatus.PENDING);
        user.setEnabled(false);
        user.setLocked(false);

        return UserResponse.from(this.userRepository.save(user));
    }

    @Transactional
    public UserResponse approveUser(UUID userId, ApproveUserRequest approveUserRequest) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getUserStatus() != UserStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is not pending approval");
        }

        user.setRoles(new HashSet<>(approveUserRequest.roles()));
        user.setUserStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        return UserResponse.from(user); // managed entity — flushed on commit
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findUsersByStatus(UserStatus status) {
        return this.userRepository.findByUserStatus(status).stream()
                .map(UserResponse::from)
                .toList();
    }
}
