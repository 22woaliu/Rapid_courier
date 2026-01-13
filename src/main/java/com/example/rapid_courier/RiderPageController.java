package com.example.rapid_courier;

import com.example.rapid_courier.model.Courier;
import com.example.rapid_courier.service.CourierApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class RiderPageController {
    
    @FXML private Label totalOrdersLabel;
    @FXML private Label pickedOrdersLabel;
    @FXML private Label inTransitOrdersLabel;
    @FXML private Label deliveredOrdersLabel;
    
    private CourierApiService courierApiService;
    
    public RiderPageController() {
        courierApiService = new CourierApiService();
    }

    @FXML
    public void initialize() {
        // Initialization logic if needed
        System.out.println("Rider Page loaded successfully!");
        
        // Load order counts
        loadOrderCounts();
    }
    
    private void loadOrderCounts() {
        try {
            List<Courier> couriers = courierApiService.getAllCouriers();
            
            int total = couriers.size();
            int picked = (int) couriers.stream().filter(c -> "Picked".equals(c.getStatus())).count();
            int inTransit = (int) couriers.stream().filter(c -> "In Transit".equals(c.getStatus())).count();
            int delivered = (int) couriers.stream().filter(c -> "Delivered".equals(c.getStatus())).count();
            
            Platform.runLater(() -> {
                totalOrdersLabel.setText(String.valueOf(total));
                pickedOrdersLabel.setText(String.valueOf(picked));
                inTransitOrdersLabel.setText(String.valueOf(inTransit));
                deliveredOrdersLabel.setText(String.valueOf(delivered));
            });
            
        } catch (IOException e) {
            System.err.println("Failed to load order counts: " + e.getMessage());
        }
    }

    @FXML
    protected void onViewOrder(ActionEvent event) {
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


    @FXML
    protected void onLogoutClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to logout");
        }
    }


    @FXML
    protected void onMouseEntered(MouseEvent event) {
        Node node = (Node) event.getSource();
        node.setStyle("-fx-background-color: transparent; -fx-text-fill: #4CAF50; -fx-cursor: hand;");
    }

    @FXML
    protected void onMouseExited(MouseEvent event) {
        Node node = (Node) event.getSource();
        node.setStyle("-fx-background-color: transparent;");
    }

    @FXML
    protected void onCardMouseEntered(MouseEvent event) {
        Node node = (Node) event.getSource();
        node.setStyle("-fx-background-color: #FFFFFF; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5); -fx-cursor: hand;");
    }

    @FXML
    protected void onCardMouseExited(MouseEvent event) {
        Node node = (Node) event.getSource();
        node.setStyle("-fx-background-color: #FFFFFF;");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
