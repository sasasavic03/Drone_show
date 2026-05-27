package org.droneshow.user_service.service;

import org.droneshow.user_service.dto.PaginatedResponse;
import org.droneshow.user_service.dto.UpdateUserRequest;
import org.droneshow.user_service.dto.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(String token);

    UserResponse updateUser(String token, UpdateUserRequest request);

    PaginatedResponse<UserResponse> getAllUsers(int page, int size, String role);

    UserResponse getUserById(Long userId);

    void deleteUser(String token, Long userId);

    void makeAdmin(String token, Long userId);
}

