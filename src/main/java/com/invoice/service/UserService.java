package com.invoice.service;

import com.invoice.dto.SignupRequest;
import com.invoice.model.User;
import com.invoice.model.UserRole;
import com.invoice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (signupRequest.getEmail() != null && userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already registered!");
        }

        User user = new User();
        // Extract username as first name if no first name provided
        String[] nameParts = signupRequest.getUsername().split(" ");
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : nameParts[0]);
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setCompanyName(signupRequest.getCompanyName());
        user.setRole(UserRole.USER);

        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.isAdmin()) {
            throw new RuntimeException("Cannot delete admin user");
        }
        
        userRepository.delete(user);
    }

    @PostConstruct
    public void createAdminUser() {
        // Check if admin exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // Change this password in production
            adminUser.setEmail("admin@invoice.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setRole(UserRole.ADMIN);
            adminUser.setPhoneNumber("1234567890");
            adminUser.setCompanyName("Invoice System");
            userRepository.save(adminUser);
        }
    }
} 