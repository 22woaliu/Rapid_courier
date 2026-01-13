package com.example.rapid_courier;

import com.example.rapid_courier.model.Courier;
import com.example.rapid_courier.service.CourierApiService;
import com.example.rapid_courier.service.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientConfirmOrderController {
    
    @FXML
    private Label pickupAddressLabel;
    
    @FXML
    private Label deliveryAddressLabel;
    
    @FXML
    private Label senderPhoneLabel;
    
    @FXML
    private Label receiverPhoneLabel;
    
    @FXML
    private Label packageWeightLabel;
    
    @FXML
    private Label packageDescriptionLabel;
    
    private Courier courierData;
    private CourierApiService courierApiService;
    
    public ClientConfirmOrderController() {
        courierApiService = new CourierApiService();
    }
    
    @FXML
    public void initialize() {
        // This is called after FXML elements are injected
        displayCourierData();
    }
    
    // Method to receive courier data from RequestCourierController
    public void setCourierData(Courier courier) {
        this.courierData = courier;
        displayCourierData();
    }
    
    private void displayCourierData() {
        if (courierData != null && pickupAddressLabel != null) {
            pickupAddressLabel.setText(courierData.getPickupAddress());
            deliveryAddressLabel.setText(courierData.getDeliveryAddress());
            senderPhoneLabel.setText(courierData.getSenderPhone());
            receiverPhoneLabel.setText(courierData.getReceiverPhone());
            packageWeightLabel.setText(courierData.getPackageWeight());
            packageDescriptionLabel.setText(courierData.getPackageDescription());
        }
    }
    
    @FXML
    protected void onBackClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client_request_courier.fxml"));
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
    protected void onConfirmOrder(ActionEvent event) {
        if (courierData == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No Order Data", "No order data available. Please go back and fill in the form.");
            return;
        }
        
        // Check if user is logged in
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            showAlert(Alert.AlertType.ERROR, "Session Error", "Not Logged In", "Please login first.");
            return;
        }
        
        try {
            // Set user email to link this order to the logged-in user
            courierData.setUserEmail(session.getEmail());
            
            // Set dummy values for required name and email fields (these are for the courier entity itself)
            courierData.setName("Order-" + System.currentTimeMillis());
            courierData.setEmail("order" + System.currentTimeMillis() + "@rapidcourier.com");
            
            // Save courier/order to database via API
            Courier createdCourier = courierApiService.createCourier(courierData);
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order Created!", 
                     "Your order has been successfully created with ID: " + createdCourier.getId());
            
            // Navigate to submission confirmation page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client_req_submit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Order Creation Failed", 
                     "Unable to create order. Please check your connection and try again.\n\nError: " + e.getMessage());
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
