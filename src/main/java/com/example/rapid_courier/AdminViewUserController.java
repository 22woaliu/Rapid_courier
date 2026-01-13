package com.example.rapid_courier;

import com.example.rapid_courier.model.User;
import com.example.rapid_courier.service.UserApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminViewUserController {
    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> addressColumn;
    @FXML private TableColumn<User, String> tokenColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private Label totalUsersLabel;

    private UserApiService userApiService;
    private ObservableList<User> usersList;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    @FXML
    public void initialize() {
        userApiService = new UserApiService();
        usersList = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadUsers();
    }

    private void setupTableColumns() {
        // Configure columns to bind to User properties using lambda expressions
        idColumn.setCellValueFactory(cellData -> {
            Long id = cellData.getValue().getId();
            return new javafx.beans.property.SimpleLongProperty(id != null ? id : 0L).asObject();
        });
        
        usernameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUsername() != null ? cellData.getValue().getUsername() : "N/A"));
        
        emailColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmail() != null ? cellData.getValue().getEmail() : "N/A"));
        
        phoneColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPhoneNumber() != null ? cellData.getValue().getPhoneNumber() : "N/A"));
        
        roleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRole() != null ? cellData.getValue().getRole() : "N/A"));
        
        addressColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAddress() != null ? cellData.getValue().getAddress() : "N/A"));
        
        tokenColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRiderToken() != null ? cellData.getValue().getRiderToken() : ""));
        
        // Format the createdAt column
        createdAtColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null) {
                String formattedDate = cellData.getValue().getCreatedAt().format(dateFormatter);
                return new SimpleStringProperty(formattedDate);
            }
            return new SimpleStringProperty("N/A");
        });
        
        // Style the role column with colors
        roleColumn.setCellFactory(column -> new javafx.scene.control.TableCell<User, String>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(role);
                    switch (role.toUpperCase()) {
                        case "ADMIN":
                            setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-font-weight: bold; -fx-padding: 5;");
                            break;
                        case "CLIENT":
                            setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-font-weight: bold; -fx-padding: 5;");
                            break;
                        case "RIDER":
                            setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-font-weight: bold; -fx-padding: 5;");
                            break;
                        default:
                            setStyle("-fx-padding: 5;");
                    }
                }
            }
        });
    }

    private void loadUsers() {
        // Fetch users in background thread
        new Thread(() -> {
            try {
                List<User> users = userApiService.getAllUsers();
                
                // Update UI on JavaFX Application Thread
                Platform.runLater(() -> {
                    usersList.clear();
                    usersList.addAll(users);
                    userTableView.setItems(usersList);
                    
                    // Update total count
                    totalUsersLabel.setText("Total Users: " + users.size());
                    
                    // Count by role
                    long clients = users.stream().filter(u -> "CLIENT".equalsIgnoreCase(u.getRole())).count();
                    long riders = users.stream().filter(u -> "RIDER".equalsIgnoreCase(u.getRole())).count();
                    long admins = users.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getRole())).count();
                    
                    totalUsersLabel.setText(String.format("Total Users: %d (Clients: %d, Riders: %d, Admins: %d)", 
                        users.size(), clients, riders, admins));
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert(AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
                });
            }
        }).start();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-page.fxml"));
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
