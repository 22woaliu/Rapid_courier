package com.rapidcourier.controller;

import com.rapidcourier.dto.LoginRequest;
import com.rapidcourier.dto.LoginResponse;
import com.rapidcourier.entity.User;
import com.rapidcourier.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> adminLogin(@RequestBody LoginRequest request) {
        LoginResponse response = authService.adminLogin(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody LoginRequest request) {
        LoginResponse response = authService.userLogin(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/rider/login")
    public ResponseEntity<LoginResponse> riderLogin(@RequestBody LoginRequest request) {
        request.setUserType("RIDER");
        LoginResponse response = authService.userLogin(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User created = authService.createUser(user);
        return ResponseEntity.ok(created);
    }
}
