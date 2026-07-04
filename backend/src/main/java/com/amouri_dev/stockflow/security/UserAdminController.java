package com.amouri_dev.stockflow.security;

import com.amouri_dev.stockflow.common.UserStatus;
import com.amouri_dev.stockflow.security.dto.ApproveUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserService userService;

    @GetMapping("/pending")
    public ResponseEntity<?> pendingUsers() {
        return ResponseEntity.ok(userService.findUsersByStatus(UserStatus.PENDING));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable UUID id, @RequestBody @Valid ApproveUserRequest approveUserRequest) {
        return ResponseEntity.ok(userService.approveUser(id, approveUserRequest));
    }
}
