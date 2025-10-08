package com.example.curiolearn.auth.service;

import com.example.curiolearn.exception.InvalidCredentialsException;
import com.example.curiolearn.exception.ResourceAlreadyExistsException;
import com.example.curiolearn.exception.ResourceNotFoundException;
import com.example.curiolearn.security.JwtService;
import com.example.curiolearn.auth.dto.AuthResponse;
import com.example.curiolearn.auth.dto.LoginRequest;
import com.example.curiolearn.auth.dto.UserDetailsDto;
import com.example.curiolearn.auth.dto.UserRegisterRequest;
import com.example.curiolearn.user.entity.Role;
import com.example.curiolearn.user.entity.User;
import com.example.curiolearn.user.repository.RoleRepository;
import com.example.curiolearn.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public AuthResponse registerStudent(UserRegisterRequest request) {
        return register(request, Role.RoleName.STUDENT);
    }

    @Transactional
    public AuthResponse registerInstructor(UserRegisterRequest request) {
        return register(request, Role.RoleName.INSTRUCTOR);
    }

    @Transactional
    public AuthResponse registerAdmin(UserRegisterRequest request) {
        return register(request, Role.RoleName.ADMIN);
    }

    private AuthResponse register(UserRegisterRequest request, Role.RoleName roleName) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered with: " + request.getEmail());
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))
                .enabled(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .userDetailsDto(UserDetailsDto.builder()
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .roles(user.getRoles().stream()
                                .map(r -> r.getName().name())
                                .collect(Collectors.toSet()))
                        .build())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()))
                .build();

        return AuthResponse.builder()
                .token(token)
                .userDetailsDto(userDetailsDto)
                .build();
    }

    @Transactional(readOnly = true)
    public UserDetailsDto getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        return UserDetailsDto.builder()
                .email(user.get().getEmail())
                .fullName(user.get().getFullName())
                .roles(user.get().getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()))
                .build();
    }
}
