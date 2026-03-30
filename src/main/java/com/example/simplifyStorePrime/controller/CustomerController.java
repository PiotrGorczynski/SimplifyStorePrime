package com.example.simplifyStorePrime.controller;

import com.example.simplifyStorePrime.dto.CustomerDTO;
import com.example.simplifyStorePrime.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final static Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Integer id) {
        log.info("Getting customer with id: {}", id);
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO dto) {
        CustomerDTO created = customerService.createCustomer(dto);
        log.info("Creating new customer: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Integer id,
                                                      @RequestBody CustomerDTO dto) {
        log.info("Updated customer with id: {}", dto.getId());
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        log.info("Returned all customers from database");
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        log.info("Deleting customer with id: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
