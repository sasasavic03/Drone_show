package org.droneshow.user_service.controller;

import org.droneshow.user_service.dto.ApiResponse;
import org.droneshow.user_service.dto.PaginatedResponse;
import org.droneshow.user_service.dto.UpdateUserRequest;
import org.droneshow.user_service.dto.UserResponse;
import org.droneshow.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get current user profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        UserResponse userResponse = userService.getCurrentUser(token);
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", userResponse));
    }

    /**
     * Update current user profile
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUserRequest request) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        UserResponse userResponse = userService.updateUser(token, request);
        return ResponseEntity.ok(ApiResponse.success("User profile updated", userResponse));
    }

    /**
     * Get all users (ADMIN ONLY)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getAllUsers(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        // Get current user to verify admin
        userService.getCurrentUser(token); // This will throw if unauthorized

        PaginatedResponse<UserResponse> response = userService.getAllUsers(page, size, role);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", response));
    }

    /**
     * Get user by ID (ADMIN ONLY)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        // Get current user to verify admin
        userService.getCurrentUser(token); // This will throw if unauthorized

        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved", userResponse));
    }

    /**
     * Delete user (ADMIN ONLY)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        userService.deleteUser(token, id);
        return ResponseEntity.ok(ApiResponse.success("User deleted"));
    }

    /**
     * Make user admin (ADMIN ONLY)
     */
    @PostMapping("/{id}/make-admin")
    public ResponseEntity<ApiResponse<Void>> makeUserAdmin(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        userService.makeAdmin(token, id);
        return ResponseEntity.ok(ApiResponse.success("User is now admin"));
    }
}

