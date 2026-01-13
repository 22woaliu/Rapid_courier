package com.example.rapid_courier;

import com.example.rapid_courier.model.Courier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RequestCourierController {
    
    @FXML
    private TextField pickupAddressField;
    
    @FXML
    private TextField deliveryAddressField;
    
    @FXML
    private TextField senderPhoneField;
    
    @FXML
    private TextField receiverPhoneField;
    
    @FXML
    private TextField packageWeightField;
    
    @FXML
    private TextField packageDescriptionField;
    
    @FXML
    protected void onBackandCancelClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client_page.fxml"));
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
    protected void onConfirmRequest(ActionEvent event) {
        // Validate all fields are filled
        if (pickupAddressField.getText().trim().isEmpty() ||
            deliveryAddressField.getText().trim().isEmpty() ||
            senderPhoneField.getText().trim().isEmpty() ||
            receiverPhoneField.getText().trim().isEmpty() ||
            packageWeightField.getText().trim().isEmpty() ||
            packageDescriptionField.getText().trim().isEmpty()) {
            
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!", "Please fill in all fields before confirming.");
            return;
        }
        
        try {
            // Create courier object from form data
            Courier courier = new Courier();
            courier.setPickupAddress(pickupAddressField.getText().trim());
            courier.setDeliveryAddress(deliveryAddressField.getText().trim());
            courier.setSenderPhone(senderPhoneField.getText().trim());
            courier.setReceiverPhone(receiverPhoneField.getText().trim());
            courier.setPackageWeight(packageWeightField.getText().trim());
            courier.setPackageDescription(packageDescriptionField.getText().trim());
            
            // Load confirmation page and pass data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client_confirm_order.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the courier data
            ClientConfirmOrderController confirmController = loader.getController();
            confirmController.setCourierData(courier);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation Error", "Unable to load confirmation page.");
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
