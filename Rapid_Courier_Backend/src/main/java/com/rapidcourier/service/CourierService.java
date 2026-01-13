package com.rapidcourier.service;

import com.rapidcourier.entity.Courier;
import com.rapidcourier.repository.CourierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourierService {
    
    @Autowired
    private CourierRepository courierRepository;
    
    public List<Courier> getAllCouriers() {
        return courierRepository.findAll();
    }
    
    public List<Courier> getCouriersByUserEmail(String userEmail) {
        return courierRepository.findByUserEmail(userEmail);
    }
    
    public Optional<Courier> getCourierById(Long id) {
        return courierRepository.findById(id);
    }
    
    public Courier createCourier(Courier courier) {
        return courierRepository.save(courier);
    }
    
    public Courier updateCourier(Long id, Courier courierDetails) {
        Courier courier = courierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Courier not found with id: " + id));
        
        courier.setName(courierDetails.getName());
        courier.setEmail(courierDetails.getEmail());
        courier.setPhoneNumber(courierDetails.getPhoneNumber());
        courier.setPickupAddress(courierDetails.getPickupAddress());
        courier.setDeliveryAddress(courierDetails.getDeliveryAddress());
        courier.setSenderPhone(courierDetails.getSenderPhone());
        courier.setReceiverPhone(courierDetails.getReceiverPhone());
        courier.setPackageWeight(courierDetails.getPackageWeight());
        courier.setPackageDescription(courierDetails.getPackageDescription());
        courier.setStatus(courierDetails.getStatus());
        
        return courierRepository.save(courier);
    }
    
    public void deleteCourier(Long id) {
        courierRepository.deleteById(id);
    }
}
