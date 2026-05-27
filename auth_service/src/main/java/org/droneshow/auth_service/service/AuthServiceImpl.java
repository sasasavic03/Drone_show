package org.droneshow.auth_service.service;

import org.droneshow.auth_service.dto.*;
import org.droneshow.auth_service.exception.AuthException;
import org.droneshow.auth_service.exception.InvalidTokenException;
import org.droneshow.auth_service.exception.ResourceNotFoundException;
import org.droneshow.auth_service.exception.UserAlreadyExistsException;
import org.droneshow.auth_service.model.RefreshToken;
import org.droneshow.auth_service.model.User;
import org.droneshow.auth_service.model.UserRole;
import org.droneshow.auth_service.repository.RefreshTokenRepository;
import org.droneshow.auth_service.repository.UserRepository;
import org.droneshow.auth_service.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                          RefreshTokenRepository refreshTokenRepository,
                          JwtUtil jwtUtil,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(UserRole.USER)
                .isDeleted(false)
                .build();

        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Save refresh token to database
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // Build response
        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .role(user.getRole().toString())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Invalid email or password");
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Save refresh token to database
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // Build response
        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .role(user.getRole().toString())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        // Find refresh token in database
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        // Check if expired
        if (refreshTokenEntity.isExpired()) {
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        // Get user
        User user = refreshTokenEntity.getUser();

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().toString());

        // Build response
        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .role(user.getRole().toString())
                .build();

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Return same refresh token
                .user(userResponse)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate and delete refresh token
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Override
    public UserResponse getCurrentUser(String token) {
        // Validate token
        if (!jwtUtil.validateAccessToken(token)) {
            throw new InvalidTokenException("Invalid access token");
        }

        // Get user ID from token
        Long userId = jwtUtil.getUserIdFromAccessToken(token);

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Build response
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .city(user.getCity())
                .role(user.getRole().toString())
                .build();
    }
}

