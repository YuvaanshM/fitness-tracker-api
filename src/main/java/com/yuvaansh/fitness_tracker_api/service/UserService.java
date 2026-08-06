package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.AuthResponse;
import com.yuvaansh.fitness_tracker_api.dto.LoginRequest;
import com.yuvaansh.fitness_tracker_api.dto.RegisterRequest;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.exception.DuplicateUsernameException;
import com.yuvaansh.fitness_tracker_api.exception.InvalidCredentialsException;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.security.JwtService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        User newUser = new User(
                request.getUsername().trim(),
                passwordEncoder.encode(request.getPassword()),
                request.getSex(),
                request.getHeight(),
                request.getWeight(),
                request.getActivityLevel(),
                request.getGoal(),
                request.getGoalWeightChangePerWeek(),
                request.getDateOfBirth()
        );

        try {
            User saved = userRepository.save(newUser);
            String token = jwtService.generateToken(saved.getUsername(), saved.getId());
            return new AuthResponse(
                    "User registered successfully",
                    saved.getId(),
                    saved.getUsername(),
                    token
            );
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateUsernameException();
        }
    }

    public AuthResponse login(LoginRequest request) {
        var userOpt = userRepository.findByUsername(request.getUsername().trim());
        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException();
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtService.generateToken(user.getUsername(), user.getId());
        return new AuthResponse("Login successful", user.getId(), user.getUsername(), token);
    }
}
