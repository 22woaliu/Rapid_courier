package com.example.rapid_courier.service;

import com.example.rapid_courier.model.LoginRequest;
import com.example.rapid_courier.model.LoginResponse;
import com.example.rapid_courier.model.SignUpRequest;

import java.io.IOException;

public class AuthApiService {
    
    private final ApiClient apiClient;
    
    public AuthApiService() {
        this.apiClient = new ApiClient();
    }
    
    public LoginResponse adminLogin(String username, String password) throws IOException {
        LoginRequest request = new LoginRequest(username, password);
        return apiClient.post("/auth/admin/login", request, LoginResponse.class);
    }
    
    public LoginResponse userLogin(String username, String password, String userType) throws IOException {
        LoginRequest request = new LoginRequest(username, password, userType);
        System.out.println("haloom");
        return apiClient.post("/auth/user/login", request, LoginResponse.class);
    }
    
    public LoginResponse riderLogin(String username, String password, String token, String usertype) throws IOException {
        LoginRequest request = new LoginRequest(username, password, token, usertype);
        return apiClient.post("/auth/rider/login", request, LoginResponse.class);
    }
    
    public void register(SignUpRequest signUpRequest) throws IOException {
        apiClient.post("/auth/register", signUpRequest, Object.class);
    }
    
    public void close() throws IOException {
        apiClient.close();
    }
}
