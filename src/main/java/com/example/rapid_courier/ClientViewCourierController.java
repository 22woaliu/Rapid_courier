package com.example.rapid_courier;

import com.example.rapid_courier.model.Courier;
import com.example.rapid_courier.service.CourierApiService;
import com.example.rapid_courier.service.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientViewCourierController {
    @FXML private VBox courierListVBox;
    
    private CourierApiService courierApiService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    @FXML
    public void initialize() {
        courierApiService = new CourierApiService();
        loadUserCouriers();
    }
    
    private void loadUserCouriers() {
        // Get current user's email from session
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            showAlert(AlertType.ERROR, "Session Error", "Please login first");
            return;
        }
        
        String userEmail = session.getEmail();
        
        // Fetch couriers in background thread
        new Thread(() -> {
            try {
                List<Courier> couriers = courierApiService.getCouriersByUserEmail(userEmail);
                
                // Update UI on JavaFX Application Thread
                Platform.runLater(() -> {
                    displayCouriers(couriers);
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert(AlertType.ERROR, "Error", "Failed to load courier orders: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void displayCouriers(List<Courier> couriers) {
        // Clear existing items (keep the first child which is the template)
        if (courierListVBox.getChildren().size() > 0) {
            courierListVBox.getChildren().clear();
        }
        
        if (couriers == null || couriers.isEmpty()) {
            Label emptyLabel = new Label("No courier orders found");
            emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6B7280; -fx-padding: 40;");
            courierListVBox.getChildren().add(emptyLabel);
            return;
        }
        
        // Create a card for each courier
        for (Courier courier : couriers) {
            VBox courierCard = createCourierCard(courier);
            courierListVBox.getChildren().add(courierCard);
        }
    }
    
    private VBox createCourierCard(Courier courier) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 14; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0.3, 0, 5);");
        
        // First HBox - Order header
        HBox headerBox = new HBox(12.0);
        headerBox.setStyle("-fx-padding: 20;");
        
        Rectangle icon = new Rectangle(34.0, 34.0);
        icon.setArcHeight(8.0);
        icon.setArcWidth(8.0);
        icon.setFill(javafx.scene.paint.Color.web("#E0F2FF"));
        icon.setStroke(javafx.scene.paint.Color.web("#1f93ff"));
        
        VBox orderInfo = new VBox(4.0);
        orderInfo.setStyle("-fx-hgrow: always;");
        Label orderNumber = new Label("Order #" + courier.getId());
        orderNumber.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        String dateText = courier.getCreatedAt() != null 
            ? courier.getCreatedAt().format(dateFormatter)
            : "N/A";
        Label dateTime = new Label(dateText);
        dateTime.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12;");
        orderInfo.getChildren().addAll(orderNumber, dateTime);
        
        Label statusLabel = new Label(courier.getStatus() != null ? courier.getStatus() : "Pending");
        String statusColor = getStatusColor(courier.getStatus());
        statusLabel.setStyle("-fx-background-color: " + statusColor + "; -fx-padding: 6 12; " +
                           "-fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 12;");
        
        headerBox.getChildren().addAll(icon, orderInfo, statusLabel);
        
        // Second HBox - Addresses
        HBox addressBox = new HBox(200.0);
        addressBox.setStyle("-fx-padding: 20;");
        
        VBox pickupBox = new VBox(6.0);
        Label pickupTitle = new Label("Pickup Address");
        pickupTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        Label pickupAddress = new Label(courier.getPickupAddress() != null ? courier.getPickupAddress() : "N/A");
        pickupAddress.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12;");
        pickupAddress.setWrapText(true);
        pickupAddress.setMaxWidth(250);
        pickupBox.getChildren().addAll(pickupTitle, pickupAddress);
        
        VBox deliveryBox = new VBox(6.0);
        Label deliveryTitle = new Label("Delivery Address");
        deliveryTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        Label deliveryAddress = new Label(courier.getDeliveryAddress() != null ? courier.getDeliveryAddress() : "N/A");
        deliveryAddress.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12;");
        deliveryAddress.setWrapText(true);
        deliveryAddress.setMaxWidth(250);
        deliveryBox.getChildren().addAll(deliveryTitle, deliveryAddress);
        
        addressBox.getChildren().addAll(pickupBox, deliveryBox);
        
        // Add package details if available
        if (courier.getPackageDescription() != null || courier.getPackageWeight() != null) {
            HBox packageBox = new HBox(200.0);
            packageBox.setStyle("-fx-padding: 0 20 20 20;");
            
            if (courier.getPackageDescription() != null) {
                VBox descBox = new VBox(6.0);
                Label descTitle = new Label("Package Description");
                descTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
                Label descText = new Label(courier.getPackageDescription());
                descText.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12;");
                descText.setWrapText(true);
                descText.setMaxWidth(250);
                descBox.getChildren().addAll(descTitle, descText);
                packageBox.getChildren().add(descBox);
            }
            
            if (courier.getPackageWeight() != null) {
                VBox weightBox = new VBox(6.0);
                Label weightTitle = new Label("Package Weight");
                weightTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
                Label weightText = new Label(courier.getPackageWeight());
                weightText.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12;");
                weightBox.getChildren().addAll(weightTitle, weightText);
                packageBox.getChildren().add(weightBox);
            }
            
            card.getChildren().addAll(headerBox, addressBox, packageBox);
        } else {
            card.getChildren().addAll(headerBox, addressBox);
        }
        
        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 14; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0.4, 0, 6); " +
                         "-fx-cursor: hand;");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 14; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0.3, 0, 5);");
        });
        
        // Add click handler to navigate to order details
        card.setOnMouseClicked(e -> {
            openOrderDetails(courier);
        });
        
        return card;
    }
    
    private void openOrderDetails(Courier courier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client_order_details.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the courier data
            ClientOrderDetailsController controller = loader.getController();
            controller.setCourierData(courier);
            
            Stage stage = (Stage) courierListVBox.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to open order details: " + e.getMessage());
        }
    }
    
    private String getStatusColor(String status) {
        if (status == null) return "#FEF3C7";
        
        switch (status.toLowerCase()) {
            case "delivered":
                return "#D1FAE5";
            case "in transit":
            case "picked":
                return "#DBEAFE";
            case "pending":
                return "#FEF3C7";
            case "cancelled":
                return "#FEE2E2";
            default:
                return "#F3F4F6";
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
    protected void onBackClick(ActionEvent event) {
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
}
