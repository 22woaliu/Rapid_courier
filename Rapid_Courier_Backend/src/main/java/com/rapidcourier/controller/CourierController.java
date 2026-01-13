package com.rapidcourier.controller;

import com.rapidcourier.entity.Courier;
import com.rapidcourier.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/couriers")
@CrossOrigin(origins = "*")
public class CourierController {
    
    @Autowired
    private CourierService courierService;
    
    @GetMapping
    public List<Courier> getAllCouriers() {
        return courierService.getAllCouriers();
    }
    
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<Courier>> getCouriersByUserEmail(@PathVariable String userEmail) {
        List<Courier> couriers = courierService.getCouriersByUserEmail(userEmail);
        return ResponseEntity.ok(couriers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Courier> getCourierById(@PathVariable Long id) {
        return courierService.getCourierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Courier> createCourier(@RequestBody Courier courier) {
        Courier created = courierService.createCourier(courier);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Courier> updateCourier(@PathVariable Long id, @RequestBody Courier courier) {
        try {
            Courier updated = courierService.updateCourier(id, courier);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourier(@PathVariable Long id) {
        courierService.deleteCourier(id);
        return ResponseEntity.noContent().build();
    }
}
