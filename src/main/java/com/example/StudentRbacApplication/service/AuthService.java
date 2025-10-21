package com.example.StudentRbacApplication.service;
import com.example.StudentRbacApplication.dto.AuthRequest;
import com.example.StudentRbacApplication.dto.AuthResponse;
import com.example.StudentRbacApplication.dto.RegisterRequest;
import com.example.StudentRbacApplication.model.Role;
import com.example.StudentRbacApplication.model.User;
import com.example.StudentRbacApplication.repo.UserRepository;
import com.example.StudentRbacApplication.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        
        // 🔥 FIX: Allow setting role from request (default to STUDENT)

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        } else {
            user.setRole(Role.STUDENT);
        }
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token);
    }
}