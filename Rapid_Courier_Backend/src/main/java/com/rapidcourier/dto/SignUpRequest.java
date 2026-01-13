package com.rapidcourier.dto;

public class SignUpRequest {
    private String username;
    private String password;
    private String email;
    private String role;
    private String phoneNumber;
    private String address;
    private String riderToken;

    public SignUpRequest() {}

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRiderToken() { return riderToken; }
    public void setRiderToken(String riderToken) { this.riderToken = riderToken; }
}
