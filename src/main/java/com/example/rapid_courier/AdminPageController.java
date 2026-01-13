package com.example.rapid_courier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPageController {

    @FXML
    public void initialize() {
        // Initialization logic if needed
        System.out.println("Admin Page loaded successfully!");
    }

    @FXML
    protected void onOrdersClick(ActionEvent event) {
        System.out.println("Orders clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-order.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to load orders page");
        }
    }

    @FXML
    protected void onUsersClick(ActionEvent event) {
        System.out.println("Users clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view-users.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to load users page");
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
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to logout");
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

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
