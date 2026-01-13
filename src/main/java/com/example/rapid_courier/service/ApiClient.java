package com.example.rapid_courier.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class ApiClient {
    
    private static final String BASE_URL = "http://localhost:8080/api";
    private final CloseableHttpClient httpClient;
    private final Gson gson;
    
    public ApiClient() {
        this.httpClient = HttpClients.createDefault();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
    
    public <T> T get(String endpoint, Class<T> responseType) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + endpoint);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return gson.fromJson(responseBody, responseType);
        } catch (ParseException e) {
            throw new IOException("Failed to parse response", e);
        }
    }
    
    public <T> T get(String endpoint, Type typeToken) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + endpoint);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return gson.fromJson(responseBody, typeToken);
        } catch (ParseException e) {
            throw new IOException("Failed to parse response", e);
        }
    }
    
    public <T> T post(String endpoint, Object body, Class<T> responseType) throws IOException {
        HttpPost request = new HttpPost(BASE_URL + endpoint);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(gson.toJson(body), StandardCharsets.UTF_8));
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return gson.fromJson(responseBody, responseType);
        } catch (ParseException e) {
            throw new IOException("Failed to parse response", e);
        }
    }
    
    public <T> T put(String endpoint, Object body, Class<T> responseType) throws IOException {
        HttpPut request = new HttpPut(BASE_URL + endpoint);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(gson.toJson(body), StandardCharsets.UTF_8));
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return gson.fromJson(responseBody, responseType);
        } catch (ParseException e) {
            throw new IOException("Failed to parse response", e);
        }
    }
    
    public void delete(String endpoint) throws IOException {
        HttpDelete request = new HttpDelete(BASE_URL + endpoint);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    public void close() throws IOException {
        httpClient.close();
    }
}
