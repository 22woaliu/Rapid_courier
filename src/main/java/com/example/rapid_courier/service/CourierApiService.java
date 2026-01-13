package com.example.rapid_courier.service;

import com.example.rapid_courier.model.Courier;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class CourierApiService {
    
    private final ApiClient apiClient;
    
    public CourierApiService() {
        this.apiClient = new ApiClient();
    }
    
    public List<Courier> getAllCouriers() throws IOException {
        Type listType = new TypeToken<List<Courier>>(){}.getType();
        return apiClient.get("/couriers", listType);
    }
    
    public List<Courier> getCouriersByUserEmail(String userEmail) throws IOException {
        Type listType = new TypeToken<List<Courier>>(){}.getType();
        return apiClient.get("/couriers/user/" + userEmail, listType);
    }
    
    public Courier getCourierById(Long id) throws IOException {
        return apiClient.get("/couriers/" + id, Courier.class);
    }
    
    public Courier createCourier(Courier courier) throws IOException {
        return apiClient.post("/couriers", courier, Courier.class);
    }
    
    public Courier updateCourier(Long id, Courier courier) throws IOException {
        return apiClient.put("/couriers/" + id, courier, Courier.class);
    }
    
    public void deleteCourier(Long id) throws IOException {
        apiClient.delete("/couriers/" + id);
    }
    
    public void close() throws IOException {
        apiClient.close();
    }
}
