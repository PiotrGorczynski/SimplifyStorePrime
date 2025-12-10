package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.CustomerDTO;
import com.example.simplifyStorePrime.entity.Customer;
import com.example.simplifyStorePrime.exception.ErrorMessages;
import com.example.simplifyStorePrime.mapper.CustomerMapper;
import com.example.simplifyStorePrime.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    public CustomerDTO getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND));
    }

    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer entity = customerMapper.toEntity(dto);
        Customer saved = customerRepository.save(entity);
        return customerMapper.toDTO(saved);
    }

    public CustomerDTO updateCustomer(Integer id, CustomerDTO dto) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND));

        customerMapper.updateEntity(dto, existing);
        Customer updated = customerRepository.save(existing);

        return customerMapper.toDTO(updated);
    }

    public void deleteCustomer(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND);
        }
        customerRepository.deleteById(id);
    }
}
