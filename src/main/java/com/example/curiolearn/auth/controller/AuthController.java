package com.example.curiolearn.auth.controller;

import com.example.curiolearn.auth.service.AuthService;
import com.example.curiolearn.common.ApiResponse;
import com.example.curiolearn.auth.dto.AuthResponse;
import com.example.curiolearn.auth.dto.LoginRequest;
import com.example.curiolearn.auth.dto.UserDetailsDto;
import com.example.curiolearn.auth.dto.UserRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final Environment env;

    @PostMapping("/register/student")
    public ResponseEntity<ApiResponse<AuthResponse>> registerStudent(@Valid @RequestBody UserRegisterRequest request) {
        AuthResponse authResponse = authService.registerStudent(request);

        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Student registered successfully",
                authResponse,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register/instructor")
    public ResponseEntity<ApiResponse<AuthResponse>> registerInstructor(@Valid @RequestBody UserRegisterRequest request) {
        AuthResponse authResponse = authService.registerInstructor(request);

        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Instructor registered successfully",
                authResponse,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(@RequestHeader("X-SETUP-KEY") String setupKey,
                                                                   @Valid @RequestBody UserRegisterRequest request) {
        String expectedKey = env.getProperty("app.setup.key");

        if (expectedKey == null || !expectedKey.equals(setupKey)) {
            throw new AccessDeniedException("Access denied");
        }

        AuthResponse authResponse = authService.registerAdmin(request);

        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Instructor registered successfully",
                authResponse,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Login successful",
                authResponse,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailsDto>> getCurrentUser(Authentication authentication) {
        UserDetailsDto userDetailsDto = authService.getCurrentUser(authentication);

        ApiResponse<UserDetailsDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Retrieved current user successfully",
                userDetailsDto,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
