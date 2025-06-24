package com.invoice.controller;

import com.invoice.dto.LoginRequest;
import com.invoice.dto.SignupRequest;
import com.invoice.model.User;
import com.invoice.security.JwtTokenProvider;
import com.invoice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        logger.info("Received signup request for username: {}", signupRequest.getUsername());
        try {
            User user = userService.createUser(signupRequest);
            logger.info("Successfully created user: {}", user.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", createUserResponse(user));
            response.put("token", tokenProvider.generateToken(user.getUsername()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during signup: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Received login request for username: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            User user = userService.getUserByUsername(loginRequest.getUsername());
            logger.info("Successfully authenticated user: {}", user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", jwt);
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for user: {}", loginRequest.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            logger.error("Error during login: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An error occurred during login");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("phoneNumber", user.getPhoneNumber());
        userResponse.put("companyName", user.getCompanyName());
        userResponse.put("role", user.getRole());
        userResponse.put("createdAt", user.getCreatedAt());
        userResponse.put("updatedAt", user.getUpdatedAt());
        return userResponse;
    }
} 