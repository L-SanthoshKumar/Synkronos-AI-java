package com.synkronos.ai.service;

import com.synkronos.ai.dto.AuthRequest;
import com.synkronos.ai.dto.AuthResponse;
import com.synkronos.ai.dto.UserDto;
import com.synkronos.ai.entity.User;
import com.synkronos.ai.repository.UserRepository;
import com.synkronos.ai.security.JwtTokenProvider;
import com.synkronos.ai.utils.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication service for user registration and login
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .role(User.UserRole.valueOf(request.getRole().toUpperCase()))
            .companyName(request.getCompanyName())
            .isActive(true)
            .isEmailVerified(false)
            .build();

        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(86400000L) // 24 hours
            .user(MapperUtil.mapToUserDto(user))
            .build();
    }

    /**
     * Authenticate user and generate tokens
     */
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(86400000L)
            .user(MapperUtil.mapToUserDto(user))
            .build();
    }

    /**
     * Refresh access token using refresh token
     */
    public Map<String, String> refreshToken(String refreshToken) {
        try {
            String email = tokenProvider.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            
            if (tokenProvider.validateToken(refreshToken, userDetails)) {
                String newAccessToken = tokenProvider.generateAccessToken(userDetails);
                Map<String, String> response = new HashMap<>();
                response.put("accessToken", newAccessToken);
                response.put("tokenType", "Bearer");
                return response;
            }
            throw new RuntimeException("Invalid refresh token");
        } catch (Exception e) {
            throw new RuntimeException("Error refreshing token: " + e.getMessage());
        }
    }
}

