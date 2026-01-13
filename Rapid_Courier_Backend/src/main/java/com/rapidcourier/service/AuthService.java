package com.rapidcourier.service;

import com.rapidcourier.dto.LoginRequest;
import com.rapidcourier.dto.LoginResponse;
import com.rapidcourier.entity.User;
import com.rapidcourier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    public LoginResponse adminLogin(LoginRequest request) {
        Optional<User> user = userRepository.findByUsernameAndPasswordAndRole(
            request.getUsername(), 
            request.getPassword(), 
            "ADMIN"
        );
        
        if (user.isPresent()) {
            User foundUser = user.get();
            LoginResponse.UserData userData = new LoginResponse.UserData(
                foundUser.getId(),
                foundUser.getUsername(),
                foundUser.getEmail(),
                foundUser.getRole()
            );
            return new LoginResponse(true, "Admin login successful", userData);
        } else {
            return new LoginResponse(false, "Invalid admin credentials");
        }
    }
    
    public LoginResponse userLogin(LoginRequest request) {
        String role = request.getUserType(); // CLIENT or RIDER
        String identifier = request.getUsername(); // Can be username or phone number
        
        // Try to find by username first, then by phone number
        Optional<User> user = userRepository.findByUsernameAndPasswordAndRole(
            identifier, 
            request.getPassword(), 
            role
        );
        
        // If not found by username, try phone number
        if (!user.isPresent()) {
            user = userRepository.findByPhoneNumberAndPasswordAndRole(
                identifier,
                request.getPassword(),
                role
            );
        }
        
        if (user.isPresent()) {
            User foundUser = user.get();
            
            // Additional validation for rider token
                if ("RIDER".equals(role) && request.getToken() != null) {
                if (!request.getToken().equals(foundUser.getRiderToken())) {
                    return new LoginResponse(false, "Invalid rider token");
                }
            }
            
            LoginResponse.UserData userData = new LoginResponse.UserData(
                foundUser.getId(),
                foundUser.getUsername(),
                foundUser.getEmail(),
                foundUser.getRole()
            );
            return new LoginResponse(true, "Login successful", userData);
        } else {
            return new LoginResponse(false, "Invalid credentials");
        }
    }
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
