module com.example.rapid_courier {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.base;
    requires javafx.graphics;

    requires org.controlsfx.controls;

    // HTTP Client for API communication
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.google.gson;

    // JavaFX needs this
    opens com.example.rapid_courier to javafx.fxml;

    // Gson needs this (THIS WAS MISSING)
    opens com.example.rapid_courier.model to com.google.gson;

    exports com.example.rapid_courier;
    exports com.example.rapid_courier.service;
    // exporting model is optional — remove if not used by other modules
}
