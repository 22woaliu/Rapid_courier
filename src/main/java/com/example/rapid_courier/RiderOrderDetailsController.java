package com.example.rapid_courier;

import com.example.rapid_courier.model.Courier;
import com.example.rapid_courier.service.CourierApiService;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class RiderOrderDetailsController {
    
    @FXML private Label orderNumberLabel;
    @FXML private Label pickupAddressLabel;
    @FXML private Label deliveryAddressLabel;
    @FXML private Label senderPhoneLabel;
    @FXML private Label receiverPhoneLabel;
    @FXML private Label packageWeightLabel;
    @FXML private Label packageDescriptionLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private HBox successMessageBox;
    
    private Courier courierData;
    private CourierApiService courierApiService;
    
    public RiderOrderDetailsController() {
        courierApiService = new CourierApiService();
    }
    
    @FXML
    public void initialize() {
        // Populate the status ComboBox with options
        if (statusComboBox != null) {
            statusComboBox.getItems().clear();
            statusComboBox.getItems().addAll("Picked", "In Transit", "Delivered");
        }
        
        // This is called after FXML elements are injected
        displayCourierData();
    }
    
    // Method to receive courier data
    public void setCourierData(Courier courier) {
        this.courierData = courier;
        displayCourierData();
    }
    
    private void displayCourierData() {
        if (courierData != null && orderNumberLabel != null) {
            orderNumberLabel.setText("Order #" + courierData.getId());
            pickupAddressLabel.setText(courierData.getPickupAddress() != null ? 
                                      courierData.getPickupAddress() : "N/A");
            deliveryAddressLabel.setText(courierData.getDeliveryAddress() != null ? 
                                        courierData.getDeliveryAddress() : "N/A");
            senderPhoneLabel.setText(courierData.getSenderPhone() != null ? 
                                    courierData.getSenderPhone() : "N/A");
            receiverPhoneLabel.setText(courierData.getReceiverPhone() != null ? 
                                      courierData.getReceiverPhone() : "N/A");
            packageWeightLabel.setText(courierData.getPackageWeight() != null ? 
                                      courierData.getPackageWeight() : "N/A");
            packageDescriptionLabel.setText(courierData.getPackageDescription() != null ? 
                                           courierData.getPackageDescription() : "N/A");
            
            // Set current status in ComboBox
            if (courierData.getStatus() != null && !courierData.getStatus().equals("Pending")) {
                statusComboBox.setValue(courierData.getStatus());
            }
        }
    }
    
    @FXML
    protected void onUpdateStatus(ActionEvent event) {
        String selectedStatus = statusComboBox.getValue();
        
        if (selectedStatus == null || selectedStatus.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Status Selected", "Please select a status", 
                     "Choose a status from the dropdown before updating.");
            return;
        }
        
        if (courierData == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No Order Data", 
                     "Order information is not available.");
            return;
        }
        
        try {
            // Update the courier data
            courierData.setStatus(selectedStatus);
            
            // Call API to update the courier
            Courier updatedCourier = courierApiService.updateCourier(courierData.getId(), courierData);
            
            // Update local data
            courierData = updatedCourier;
            
            // Show success message
            successMessageBox.setVisible(true);
            
            // Hide the success message after 3 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> successMessageBox.setVisible(false));
            pause.play();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to Update Status", 
                     "Could not update the order status: " + e.getMessage());
        }
    }
    
    @FXML
    protected void onBackClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rider-view-order.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
