package com.example.simplifyStorePrime.controller;

import com.example.simplifyStorePrime.dto.DeliveryDTO;
import com.example.simplifyStorePrime.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<List<DeliveryDTO>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDTO> getDeliveryById(@PathVariable Integer id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    @PostMapping
    public ResponseEntity<DeliveryDTO> createDelivery(@RequestBody DeliveryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryService.createDelivery(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryDTO> updateDelivery(@PathVariable Integer id, @RequestBody DeliveryDTO dto) {
        return ResponseEntity.ok(deliveryService.updateDelivery(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Integer id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}