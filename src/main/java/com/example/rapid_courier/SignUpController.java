package com.example.rapid_courier;

import com.example.rapid_courier.model.LoginResponse;
import com.example.rapid_courier.model.SignUpRequest;
import com.example.rapid_courier.service.AuthApiService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {

    @FXML
    private ComboBox<String> userTypeComboBox;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField addressField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private VBox riderTokenVBox;

    @FXML
    private TextField riderTokenField;

    private AuthApiService authApiService;

    @FXML
    public void initialize() {
        // Populate the ComboBox with user types
        userTypeComboBox.getItems().addAll("Client", "Rider");
        userTypeComboBox.setValue("Client"); // Set default value
        authApiService = new AuthApiService();
    }

    @FXML
    protected void onUserTypeChange(ActionEvent event) {
        String selectedUserType = userTypeComboBox.getValue();
        
        if ("Rider".equals(selectedUserType)) {
            // Show rider token field
            riderTokenVBox.setVisible(true);
            riderTokenVBox.setManaged(true);
        } else {
            // Hide rider token field
            riderTokenVBox.setVisible(false);
            riderTokenVBox.setManaged(false);
            riderTokenField.clear(); // Clear the field when hidden
        }
    }

    @FXML
    protected void onBackClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to navigate back");
        }
    }

    @FXML
    protected void onSignUpClick(ActionEvent event) {
        // Get values from all fields
        String userType = userTypeComboBox.getValue();
        String fullName = fullNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String address = addressField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate all fields are filled
        if (userType == null || userType.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a user type");
            return;
        }

        if (fullName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter your full name");
            return;
        }

        if (phoneNumber.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter your phone number");
            return;
        }

        if (address.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter your address");
            return;
        }

        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a password");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 6 characters");
            return;
        }

        if (confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please confirm your password");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match");
            return;
        }

        // Validate phone number format
        if (!phoneNumber.matches("^[+]?[0-9]{10,15}$")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid phone number");
            return;
        }

        // Validate rider token if user type is Rider
        String riderToken = null;
        if ("Rider".equals(userType)) {
            riderToken = riderTokenField.getText().trim();
            if (riderToken.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter your rider token");
                return;
            }
        }

        // Create SignUpRequest object
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername(fullName);
        signUpRequest.setPassword(password);
        signUpRequest.setEmail(phoneNumber + "@rapidcourier.com"); // Generate email from phone
        signUpRequest.setRole(userType.toUpperCase()); // CLIENT or RIDER
        signUpRequest.setPhoneNumber(phoneNumber);
        signUpRequest.setAddress(address);
        
        // Set rider token if user is a rider
        if (riderToken != null) {
            signUpRequest.setRiderToken(riderToken);
        }

        // Call API to register user
        try {
             authApiService.register(signUpRequest);
            
            // Close the API service
            try {
                authApiService.close();
            } catch (IOException closeEx) {
                closeEx.printStackTrace();
            }
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", 
                "Account created successfully! You can now login.");
            
            // Navigate to login page
            navigateToLogin(event);
            
        } catch (IOException e) {
            e.printStackTrace();
            String errorMsg = "Failed to create account. ";
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                errorMsg += "Please ensure the backend server is running.";
            } else {
                errorMsg += "Please try again.\n" + e.getMessage();
            }
            showAlert(Alert.AlertType.ERROR, "Registration Error", errorMsg);
        }
    }

    @FXML
    protected void onLoginClick(ActionEvent event) {
        navigateToLogin(event);
    }

    private void navigateToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to navigate to login page");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
