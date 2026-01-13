package com.example.rapid_courier;

import com.example.rapid_courier.model.LoginResponse;
import com.example.rapid_courier.service.AuthApiService;
import com.example.rapid_courier.service.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class UserLoginController {

    @FXML
    private ComboBox<String> userTypeComboBox;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField riderPhoneNumberField;

    @FXML
    private PasswordField riderPasswordField;

    @FXML
    private TextField riderTokenField;

    @FXML
    private VBox riderVBox;

    @FXML
    private VBox clientVBox;

    private AuthApiService authApiService;

    @FXML
    public void initialize() {
        //Populate the ComboBox with user types
        userTypeComboBox.getItems().addAll("Client", "Rider");
        authApiService = new AuthApiService();
    }

    @FXML
    protected void switchUserType(ActionEvent event) throws IOException {
        String selectedUserType = userTypeComboBox.getValue();
        if (selectedUserType.equals("Rider")) {
            riderVBox.setVisible(true);
            riderVBox.setManaged(true);

            clientVBox.setVisible(false);
            clientVBox.setManaged(false);
        }
        else {
            riderVBox.setVisible(false);
            riderVBox.setManaged(false);

            clientVBox.setVisible(true);
            clientVBox.setManaged(true);
        }

    }

    @FXML
    protected void onBackClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToClientPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client_page.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to load client page");
        }
    }

    private void navigateToRiderPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rider_page.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to load client page");
        }
    }

    @FXML
    protected void onLoginClick(ActionEvent event) {
        String userType = userTypeComboBox.getValue();
        
        // Validate user type selection
        if (userType == null || userType.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Please select a user type");
            return;
        }
        
        String phoneNumber;
        String password;
        String token = null;
        
        // Get credentials based on user type
        if ("Client".equals(userType)) {
            phoneNumber = phoneNumberField.getText().trim();
            password = passwordField.getText();
        } else { // Rider
            phoneNumber = riderPhoneNumberField.getText().trim();
            password = riderPasswordField.getText();
            token = riderTokenField.getText().trim();
        }
        
        // Validate fields are filled
        if (phoneNumber.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Please enter your phone number");
            return;
        }
        
        if (password.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Please enter your password");
            return;
        }
        
        if ("Rider".equals(userType) && token.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Please enter your rider token");
            return;
        }
        
        // Authenticate with backend
        try {
            LoginResponse response;
            
            if ("Client".equals(userType)) {
                response = authApiService.userLogin(phoneNumber, password, "CLIENT");
                // Check if login was successful
                if (response.isSuccess()) {
                    // Store user session
                    LoginResponse.UserData user = response.getUser();
                    if (user != null) {
                        UserSession.getInstance().setUserData(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getRole()
                        );
                    }
                    // Navigate to client page
                    navigateToClientPage(event);
                } else {
                    // Show error message
                    showAlert(AlertType.ERROR, "Login Failed", "Invalid number or password");
                }
            } else {
                response = authApiService.riderLogin(phoneNumber, password, token, "RIDER");
                // Check if login was successful
                if (response.isSuccess()) {
                    // Store user session
                    LoginResponse.UserData user = response.getUser();
                    if (user != null) {
                        UserSession.getInstance().setUserData(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getRole()
                        );
                    }
                    // Navigate to rider page
                    navigateToRiderPage(event);
                } else {
                    // Show error message
                    showAlert(AlertType.ERROR, "Login Failed", "Invalid number, password, or token");
                }
            }
            

            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Connection Error", 
                "Failed to connect to server. Please ensure the backend is running.");
        }
    }
    
    
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onSignUpClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
