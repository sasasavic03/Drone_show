package org.droneshow.auth_service.service;

import org.droneshow.auth_service.dto.*;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshAccessToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);

    UserResponse getCurrentUser(String token);
}

