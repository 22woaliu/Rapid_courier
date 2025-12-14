package com.example.rapid_courier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UserLoginController {
    @FXML
    private ComboBox<String> userTypeComboBox;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        // Populate the ComboBox with user types
        userTypeComboBox.getItems().addAll("Client", "Rider");
        userTypeComboBox.setValue("Choose user type");
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

    @FXML
    protected void onLoginClick() {
        String userType = userTypeComboBox.getValue();
        String phoneNumber = phoneNumberField.getText();
        String password = passwordField.getText();

        // Demo credentials validation
        boolean isValid = false;
        String message = "";

        if (userType.equals("Client") && phoneNumber.equals("+1234567890") && password.equals("password")) {
            isValid = true;
            message = "Welcome Client! Login successful.";
        } else if (userType.equals("Rider") && phoneNumber.equals("+9999888877") && password.equals("password")) {
            isValid = true;
            message = "Welcome Rider! Login successful.";
        }

        if (isValid) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Successful");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid credentials. Please check user type, phone number, and password.");
            alert.showAndWait();
        }
    }
}
