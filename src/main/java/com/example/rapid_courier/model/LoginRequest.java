package com.example.rapid_courier.model;

public class LoginRequest {
    private String username;
    private String password;
    private String userType;
    private String token;
    
    public LoginRequest() {}
    
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public LoginRequest(String username, String password, String userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public LoginRequest(String username, String password, String token, String userType) {
        this.username = username;
        this.password = password;
        this.token = token;
    }
    
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
