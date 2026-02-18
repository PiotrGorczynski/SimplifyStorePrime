package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.CustomerDTO;
import com.example.simplifyStorePrime.entity.Customer;
import com.example.simplifyStorePrime.mapper.CustomerMapper;
import com.example.simplifyStorePrime.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer sampleCustomer() {
        return Customer.builder()
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
    void getAllCustomers_shouldReturnList() {
        Customer customer = sampleCustomer();
        CustomerDTO dto = sampleDTO();

        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("Jan Kowalski", result.get(0).getInfo());
        verify(customerRepository).findAll();
    }

    @Test
    void getAllCustomers_emptyDatabase_shouldReturnEmptyList() {
        when(customerRepository.findAll()).thenReturn(List.of());

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCustomerById_existingId_shouldReturnCustomer() {
        Customer customer = sampleCustomer();
        CustomerDTO dto = sampleDTO();

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        CustomerDTO result = customerService.getCustomerById(1);

        assertNotNull(result);
        assertEquals("Jan Kowalski", result.getInfo());
    }

    @Test
    void getCustomerById_nonExistingId_shouldThrowException() {
        when(customerRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> customerService.getCustomerById(999));
    }

    @Test
    void createCustomer_validData_shouldReturnCreated() {
        Customer customer = sampleCustomer();
        CustomerDTO dto = sampleDTO();

        when(customerMapper.toEntity(dto)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        CustomerDTO result = customerService.createCustomer(dto);

        assertNotNull(result);
        assertEquals("Jan Kowalski", result.getInfo());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateCustomer_existingId_shouldReturnUpdated() {
        Customer existing = sampleCustomer();
        CustomerDTO dto = sampleDTO();
        dto.setInfo("Updated Name");

        when(customerRepository.findById(1)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(existing);
        when(customerMapper.toDTO(existing)).thenReturn(dto);

        CustomerDTO result = customerService.updateCustomer(1, dto);

        assertEquals("Updated Name", result.getInfo());
        verify(customerMapper).updateEntity(dto, existing);
    }

    @Test
    void updateCustomer_nonExistingId_shouldThrowException() {
        when(customerRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> customerService.updateCustomer(999, sampleDTO()));
    }

    @Test
    void deleteCustomer_existingId_shouldDelete() {
        when(customerRepository.existsById(1)).thenReturn(true);

        customerService.deleteCustomer(1);

        verify(customerRepository).deleteById(1);
    }

    @Test
    void deleteCustomer_nonExistingId_shouldThrowException() {
        when(customerRepository.existsById(999)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> customerService.deleteCustomer(999));
        verify(customerRepository, never()).deleteById(999);
    }
}