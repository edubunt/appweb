package com.application.appweb.controller;

import com.application.appweb.dto.request.LoginRequest;
import com.application.appweb.dto.request.UserRegisterRequest;
import com.application.appweb.dto.response.ApiResponse;
import com.application.appweb.dto.response.UserResponse;
import com.application.appweb.model.User;
import com.application.appweb.security.JwtTokenProvider;
import com.application.appweb.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.username());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            String token = jwtTokenProvider.generateToken(authentication);
            User user = userService.findByUsername(loginRequest.username());
            UserResponse userResponse = UserResponse.fromUserWithToken(user, token);

            return ResponseEntity.ok(ApiResponse.success(userResponse, "Login successful"));
        } catch (Exception ex) {
            log.error("Login failed for user: {}", loginRequest.username(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username or password", HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterRequest registerRequest) {
        log.info("Registration attempt for user: {}", registerRequest.username());
        try {
            UserResponse userResponse = userService.createUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(userResponse, "User registered successfully"));
        } catch (Exception ex) {
            log.error("Registration failed for user: {}", registerRequest.username(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }
}
