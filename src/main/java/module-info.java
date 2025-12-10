module com.example.rapid_courier {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.rapid_courier to javafx.fxml;
    exports com.example.rapid_courier;
}