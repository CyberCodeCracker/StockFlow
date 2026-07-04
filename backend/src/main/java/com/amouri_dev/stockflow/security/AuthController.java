package com.amouri_dev.stockflow.security;

import com.amouri_dev.stockflow.security.dto.LoginRequest;
import com.amouri_dev.stockflow.security.dto.RefreshRequest;
import com.amouri_dev.stockflow.security.dto.RegisterRequest;
import com.amouri_dev.stockflow.security.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.refresh(refreshRequest.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid RefreshRequest refreshRequest) {
        authService.logout(refreshRequest.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(UserResponse.from(principal.getUser()));
    }
}
