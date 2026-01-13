package com.example.rapid_courier.model;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private UserData user;
    
    public LoginResponse() {}
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public UserData getUser() { return user; }
    public void setUser(UserData user) { this.user = user; }
    
    public static class UserData {
        private Long id;
        private String username;
        private String email;
        private String role;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
