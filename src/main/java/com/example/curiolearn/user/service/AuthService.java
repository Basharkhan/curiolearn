package com.example.curiolearn.user.service;

import com.example.curiolearn.exception.ResourceAlreadyExistsException;
import com.example.curiolearn.exception.ResourceNotFoundException;
import com.example.curiolearn.security.JwtService;
import com.example.curiolearn.user.dto.AuthResponse;
import com.example.curiolearn.user.dto.UserDetailsDto;
import com.example.curiolearn.user.dto.UserRegisterRequest;
import com.example.curiolearn.user.entity.Role;
import com.example.curiolearn.user.entity.User;
import com.example.curiolearn.user.repository.RoleRepository;
import com.example.curiolearn.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

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
}
