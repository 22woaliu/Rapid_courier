package com.example.rapid_courier;

import com.example.rapid_courier.model.Courier;
import com.example.rapid_courier.service.CourierApiService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RiderViewOrderController {
    @FXML private VBox orderListVBox;
    
    private CourierApiService courierApiService;
    private Timeline autoRefreshTimeline;
    private static final int REFRESH_INTERVAL_SECONDS = 5; // Refresh every 5 seconds
    
    public RiderViewOrderController() {
        courierApiService = new CourierApiService();
    }
    
    @FXML
    public void initialize() {
        // Load orders initially
        loadOrders();
        
        // Setup auto-refresh
        setupAutoRefresh();
    }
    
    private void setupAutoRefresh() {
        // Create a Timeline that refreshes orders every REFRESH_INTERVAL_SECONDS
        autoRefreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(REFRESH_INTERVAL_SECONDS), e -> loadOrders())
        );
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();
    }
    
    private void loadOrders() {
        try {
            List<Courier> couriers = courierApiService.getAllCouriers();
            
            // Update UI on JavaFX Application Thread
            Platform.runLater(() -> {
                orderListVBox.getChildren().clear();
                
                if (couriers.isEmpty()) {
                    Label emptyLabel = new Label("No orders available at the moment.");
                    emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6B7280; -fx-padding: 40;");
                    orderListVBox.getChildren().add(emptyLabel);
                } else {
                    for (Courier courier : couriers) {
                        VBox orderCard = createOrderCard(courier);
                        orderListVBox.getChildren().add(orderCard);
                    }
                }
            });
            
        } catch (IOException e) {
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Load Orders", 
                         "Could not fetch orders from server: " + e.getMessage());
            });
        }
    }
    
    private VBox createOrderCard(Courier courier) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 14; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0.3, 0, 5); " +
                     "-fx-cursor: hand;");
        
        // Header with order number, date, and status
        HBox header = new HBox(12);
        header.setStyle("-fx-padding: 20;");
        
        Rectangle icon = new Rectangle(34, 34);
        icon.setArcHeight(8);
        icon.setArcWidth(8);
        icon.setFill(Color.web("#E0F2FF"));
        icon.setStroke(Color.web("#1f93ff"));
        
        VBox orderInfo = new VBox(4);
        Label orderNumber = new Label("Order #" + courier.getId());
        orderNumber.setStyle("-fx-font-weight: bold;");
        
        String dateTime = courier.getCreatedAt() != null ? 
            courier.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")) : 
            "N/A";
        Label dateLabel = new Label(dateTime);
        dateLabel.setStyle("-fx-text-fill: #6B7280;");
        
        orderInfo.getChildren().addAll(orderNumber, dateLabel);
        HBox.setHgrow(orderInfo, javafx.scene.layout.Priority.ALWAYS);
        
        // Determine status and styling
        String status = courier.getStatus() != null ? courier.getStatus() : "Pending";
        String statusColor;
        String statusBgColor;
        
        switch (status) {
            case "Picked":
                statusColor = "#1565C0";
                statusBgColor = "#BBDEFB";
                break;
            case "In Transit":
                statusColor = "#E65100";
                statusBgColor = "#FFE0B2";
                break;
            case "Delivered":
                statusColor = "#2E7D32";
                statusBgColor = "#C8E6C9";
                break;
            default: // Pending
                statusColor = "#92400E";
                statusBgColor = "#FEF3C7";
        }
        
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-background-color: " + statusBgColor + "; -fx-text-fill: " + statusColor + "; " +
                           "-fx-padding: 6 12; -fx-background-radius: 12; -fx-font-size: 12;");
        
        header.getChildren().addAll(icon, orderInfo, statusLabel);
        
        // Address details
        HBox addressBox = new HBox(100);
        addressBox.setStyle("-fx-padding: 0 20 20 20;");
        
        VBox pickupBox = new VBox(6);
        Label pickupLabel = new Label("Pickup Address");
        pickupLabel.setStyle("-fx-font-weight: bold;");
        Label pickupAddress = new Label(courier.getPickupAddress() != null ? 
                                       courier.getPickupAddress() : "N/A");
        pickupAddress.setStyle("-fx-text-fill: #6B7280;");
        pickupAddress.setWrapText(true);
        pickupAddress.setMaxWidth(250);
        pickupBox.getChildren().addAll(pickupLabel, pickupAddress);
        
        VBox deliveryBox = new VBox(6);
        Label deliveryLabel = new Label("Delivery Address");
        deliveryLabel.setStyle("-fx-font-weight: bold;");
        Label deliveryAddress = new Label(courier.getDeliveryAddress() != null ? 
                                         courier.getDeliveryAddress() : "N/A");
        deliveryAddress.setStyle("-fx-text-fill: #6B7280;");
        deliveryAddress.setWrapText(true);
        deliveryAddress.setMaxWidth(250);
        deliveryBox.getChildren().addAll(deliveryLabel, deliveryAddress);
        
        addressBox.getChildren().addAll(pickupBox, deliveryBox);
        
        card.getChildren().addAll(header, addressBox);
        
        // Add click handler to view order details
        card.setOnMouseClicked(event -> onOrderCardClick(courier));
        
        // Hover effects
        card.setOnMouseEntered(e -> 
            card.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 14; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0.4, 0, 6); " +
                         "-fx-cursor: hand;")
        );
        card.setOnMouseExited(e -> 
            card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 14; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0.3, 0, 5); " +
                         "-fx-cursor: hand;")
        );
        
        return card;
    }
    
    private void onOrderCardClick(Courier courier) {
        try {
            // Stop auto-refresh when navigating away
            if (autoRefreshTimeline != null) {
                autoRefreshTimeline.stop();
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rider-order-details.fxml"));
            Parent root = loader.load();
            
            // Pass courier data to details page
            RiderOrderDetailsController controller = loader.getController();
            controller.setCourierData(courier);
            
            Stage stage = (Stage) orderListVBox.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation Failed", 
                     "Could not open order details: " + e.getMessage());
        }
    }

    @FXML
    protected void onBackClick(ActionEvent event) {
        // Stop auto-refresh when navigating away
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rider_page.fxml"));
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
