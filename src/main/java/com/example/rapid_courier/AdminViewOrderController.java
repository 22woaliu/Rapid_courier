package com.example.rapid_courier;

import com.example.rapid_courier.model.Courier;
import com.example.rapid_courier.service.CourierApiService;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminViewOrderController {
    @FXML private TableView<Courier> orderTableView;
    @FXML private TableColumn<Courier, Long> idColumn;
    @FXML private TableColumn<Courier, String> userEmailColumn;
    @FXML private TableColumn<Courier, String> pickupAddressColumn;
    @FXML private TableColumn<Courier, String> deliveryAddressColumn;
    @FXML private TableColumn<Courier, String> senderPhoneColumn;
    @FXML private TableColumn<Courier, String> receiverPhoneColumn;
    @FXML private TableColumn<Courier, String> packageWeightColumn;
    @FXML private TableColumn<Courier, String> packageDescriptionColumn;
    @FXML private TableColumn<Courier, String> statusColumn;
    @FXML private TableColumn<Courier, String> createdAtColumn;
    @FXML private TableColumn<Courier, Void> actionsColumn;
    @FXML private Label totalOrdersLabel;

    private CourierApiService courierApiService;
    private ObservableList<Courier> ordersList;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    @FXML
    public void initialize() {
        courierApiService = new CourierApiService();
        ordersList = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadOrders();
    }

    private void setupTableColumns() {
        // Configure columns to bind to Courier properties using lambda expressions
        idColumn.setCellValueFactory(cellData -> {
            Long id = cellData.getValue().getId();
            return new javafx.beans.property.SimpleLongProperty(id != null ? id : 0L).asObject();
        });
        
        userEmailColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUserEmail() != null ? cellData.getValue().getUserEmail() : "N/A"));
        
        pickupAddressColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPickupAddress() != null ? cellData.getValue().getPickupAddress() : "N/A"));
        
        deliveryAddressColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDeliveryAddress() != null ? cellData.getValue().getDeliveryAddress() : "N/A"));
        
        senderPhoneColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSenderPhone() != null ? cellData.getValue().getSenderPhone() : "N/A"));
        
        receiverPhoneColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getReceiverPhone() != null ? cellData.getValue().getReceiverPhone() : "N/A"));
        
        packageWeightColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPackageWeight() != null ? cellData.getValue().getPackageWeight() : "N/A"));
        
        packageDescriptionColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPackageDescription() != null ? cellData.getValue().getPackageDescription() : "N/A"));
        
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus() != null ? cellData.getValue().getStatus() : "Pending"));
        
        // Format the createdAt column
        createdAtColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null) {
                String formattedDate = cellData.getValue().getCreatedAt().format(dateFormatter);
                return new SimpleStringProperty(formattedDate);
            }
            return new SimpleStringProperty("N/A");
        });
        
        // Style the status column with colors
        statusColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Courier, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "delivered":
                            setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-font-weight: bold; -fx-padding: 5;");
                            break;
                        case "in transit":
                        case "picked":
                            setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-font-weight: bold; -fx-padding: 5;");
                            break;
                        case "pending":
                            setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-font-weight: bold; -fx-padding: 5;");
                            break;
                        case "cancelled":
                            setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-font-weight: bold; -fx-padding: 5;");
                            break;
                        default:
                            setStyle("-fx-padding: 5;");
                    }
                }
            }
        });
        
        // Setup actions column with status update ComboBox
        actionsColumn.setCellFactory(column -> new TableCell<Courier, Void>() {
            private final ComboBox<String> statusComboBox = new ComboBox<>();
            private final Button updateButton = new Button("Update");
            private final HBox container = new HBox(8);
            
            {
                statusComboBox.getItems().addAll("Pending", "Picked", "In Transit", "Delivered");
                statusComboBox.setPrefWidth(120);
                statusComboBox.setStyle("-fx-font-size: 11;");
                
                updateButton.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; " +
                                     "-fx-font-size: 11; -fx-cursor: hand; -fx-padding: 5 10;");
                updateButton.setOnAction(event -> {
                    Courier courier = getTableView().getItems().get(getIndex());
                    String newStatus = statusComboBox.getValue();
                    if (newStatus != null && !newStatus.equals(courier.getStatus())) {
                        updateOrderStatus(courier, newStatus);
                    }
                });
                
                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(statusComboBox, updateButton);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Courier courier = getTableView().getItems().get(getIndex());
                    statusComboBox.setValue(courier.getStatus() != null ? courier.getStatus() : "Pending");
                    setGraphic(container);
                }
            }
        });
    }
    
    private void updateOrderStatus(Courier courier, String newStatus) {
        // Update in background thread
        new Thread(() -> {
            try {
                courier.setStatus(newStatus);
                courierApiService.updateCourier(courier.getId(), courier);
                
                Platform.runLater(() -> {
                    // Refresh the table to show updated status
                    orderTableView.refresh();
                    
                    // Reload orders to update statistics
                    loadOrders();
                    
                    showAlert(AlertType.INFORMATION, "Success", 
                             "Order #" + courier.getId() + " status updated to: " + newStatus);
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert(AlertType.ERROR, "Error", 
                             "Failed to update order status: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadOrders() {
        // Fetch orders in background thread
        new Thread(() -> {
            try {
                List<Courier> orders = courierApiService.getAllCouriers();
                
                // Update UI on JavaFX Application Thread
                Platform.runLater(() -> {
                    ordersList.clear();
                    ordersList.addAll(orders);
                    orderTableView.setItems(ordersList);
                    
                    // Update total count
                    totalOrdersLabel.setText("Total Orders: " + orders.size());
                    
                    // Count by status
                    long pending = orders.stream().filter(o -> "Pending".equalsIgnoreCase(o.getStatus())).count();
                    long inTransit = orders.stream().filter(o -> "In Transit".equalsIgnoreCase(o.getStatus()) || "Picked".equalsIgnoreCase(o.getStatus())).count();
                    long delivered = orders.stream().filter(o -> "Delivered".equalsIgnoreCase(o.getStatus())).count();
                    
                    totalOrdersLabel.setText(String.format("Total Orders: %d (Pending: %d, In Transit: %d, Delivered: %d)", 
                        orders.size(), pending, inTransit, delivered));
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert(AlertType.ERROR, "Error", "Failed to load orders: " + e.getMessage());
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
