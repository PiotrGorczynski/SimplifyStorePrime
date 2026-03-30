package com.example.simplifyStorePrime.controller;

import com.example.simplifyStorePrime.dto.CustomerDTO;
import com.example.simplifyStorePrime.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private CustomerDTO sampleDTO() {
        return CustomerDTO.builder()
                .id(1)
                .info("Jan Kowalski")
                .salesOrders("order")
                .invoices("yes")
                .paymentHistory("no")
                .communication("jan@test.com")
                .category("business")
                .feedback("Good")
                .notes("Notes")
                .supportRequest("None")
                .build();
    }

    @Test
    void getAllCustomers_shouldReturn200() {
        when(customerService.getAllCustomers()).thenReturn(List.of(sampleDTO()));

        ResponseEntity<List<CustomerDTO>> response = customerController.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Jan Kowalski", response.getBody().get(0).getInfo());
    }

    @Test
    void getCustomerById_existingId_shouldReturn200() {
        when(customerService.getCustomerById(1)).thenReturn(sampleDTO());

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Jan Kowalski", response.getBody().getInfo());
    }

    @Test
    void getCustomerById_nonExistingId_shouldThrowException() {
        when(customerService.getCustomerById(999))
                .thenThrow(new EntityNotFoundException("Customer not found"));

        assertThrows(EntityNotFoundException.class,
                () -> customerController.getCustomerById(999));
    }

    @Test
    void createCustomer_shouldReturn201() {
        CustomerDTO dto = sampleDTO();
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(dto);

        ResponseEntity<CustomerDTO> response = customerController.createCustomer(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Jan Kowalski", response.getBody().getInfo());
    }

    @Test
    void updateCustomer_existingId_shouldReturn200() {
        CustomerDTO dto = sampleDTO();
        dto.setInfo("Updated Name");
        when(customerService.updateCustomer(eq(1), any(CustomerDTO.class))).thenReturn(dto);

        ResponseEntity<CustomerDTO> response = customerController.updateCustomer(1, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Name", response.getBody().getInfo());
    }

    @Test
    void updateCustomer_nonExistingId_shouldThrowException() {
        when(customerService.updateCustomer(eq(999), any(CustomerDTO.class)))
                .thenThrow(new EntityNotFoundException("Customer not found"));

        assertThrows(EntityNotFoundException.class,
                () -> customerController.updateCustomer(999, sampleDTO()));
    }

    @Test
    void deleteCustomer_existingId_shouldReturn204() {
        ResponseEntity<Void> response = customerController.deleteCustomer(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerService).deleteCustomer(1);
    }

    @Test
    void deleteCustomer_nonExistingId_shouldThrowException() {
        doThrow(new EntityNotFoundException("Customer not found"))
                .when(customerService).deleteCustomer(999);

        assertThrows(EntityNotFoundException.class,
                () -> customerController.deleteCustomer(999));
    }
}