package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    public String register(RegisterRequest request) {
        if (request.getConfirmPassword() != null
                && !request.getPassword().equals(request.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already in use");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already taken");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("New user registered: {}", request.getEmail());
        return "User registered successfully";
    }

    public Map<String, String> login(LoginRequest request) {
        String identifier = request.getUsernameOrEmail();

        // Guard: if identifier is null, the request sent wrong field name
        if (identifier == null || identifier.isBlank()) {
            log.error("Login failed: usernameOrEmail field is null or blank. Check request body field name.");
            throw new RuntimeException("Invalid credentials");
        }

        log.info("Login attempt for identifier: {}", identifier);

        User user = userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> {
                    log.warn("No user found for identifier: {}", identifier);
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Password mismatch for user: {}", user.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        log.info("User logged in successfully: {}", user.getEmail());

        return Map.of(
            "token", accessToken,
            "refreshToken", refreshToken.getToken(),
            "username", user.getUsername()
        );
    }

    public Map<String, String> refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(refreshTokenStr);
        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateToken(user.getEmail());
        log.info("Access token refreshed for user: {}", user.getEmail());
        return Map.of("token", newAccessToken, "username", user.getUsername());
    }

    public void logout(String refreshTokenStr) {
        RefreshToken token = refreshTokenService.validateRefreshToken(refreshTokenStr);
        refreshTokenService.deleteByUser(token.getUser());
        log.info("User logged out: {}", token.getUser().getEmail());
    }
}
