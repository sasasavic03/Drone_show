package org.droneshow.user_service.service;

import org.droneshow.user_service.dto.PaginatedResponse;
import org.droneshow.user_service.dto.UpdateUserRequest;
import org.droneshow.user_service.dto.UserResponse;
import org.droneshow.user_service.exception.ResourceNotFoundException;
import org.droneshow.user_service.exception.UnauthorizedException;
import org.droneshow.user_service.model.User;
import org.droneshow.user_service.model.UserRole;
import org.droneshow.user_service.repository.UserRepository;
import org.droneshow.user_service.util.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserResponse getCurrentUser(String token) {
        // Validate token
        if (!jwtUtil.validateAccessToken(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        // Get user ID from token
        Long userId = jwtUtil.getUserIdFromAccessToken(token);

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Build response
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String token, UpdateUserRequest request) {
        // Validate token
        if (!jwtUtil.validateAccessToken(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        // Get user ID from token
        Long userId = jwtUtil.getUserIdFromAccessToken(token);

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update fields
        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            user.setCity(request.getCity());
        }

        // Save
        user = userRepository.save(user);

        return mapToUserResponse(user);
    }

    @Override
    public PaginatedResponse<UserResponse> getAllUsers(int page, int size, String role) {
        // Create pageable
        Pageable pageable = PageRequest.of(page, size);

        // Find users by role if specified
        Page<User> users;
        if (role != null && !role.isEmpty()) {
            try {
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                users = userRepository.findByRole(userRole, pageable);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + role);
            }
        } else {
            users = userRepository.findAllActive(pageable);
        }

        // Build response
        return PaginatedResponse.<UserResponse>builder()
                .content(users.getContent().stream()
                        .map(this::mapToUserResponse)
                        .collect(Collectors.toList()))
                .page(page)
                .size(size)
                .total(users.getTotalElements())
                .pages(users.getTotalPages())
                .first(users.isFirst())
                .last(users.isLast())
                .build();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToUserResponse(user);
    }

    @Override
    public void deleteUser(String token, Long userId) {
        // Validate token
        if (!jwtUtil.validateAccessToken(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        // Check if user is admin
        String role = jwtUtil.getRoleFromAccessToken(token);
        if (!role.equals("ADMIN")) {
            throw new UnauthorizedException("Only admins can delete users");
        }

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Soft delete
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void makeAdmin(String token, Long userId) {
        // Validate token
        if (!jwtUtil.validateAccessToken(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        // Check if user is admin
        String role = jwtUtil.getRoleFromAccessToken(token);
        if (!role.equals("ADMIN")) {
            throw new UnauthorizedException("Only admins can make other users admin");
        }

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Change role
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
    }

    private UserResponse mapToUserResponse(User user) {
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

