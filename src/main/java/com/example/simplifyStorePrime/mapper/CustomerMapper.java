package com.example.simplifyStorePrime.mapper;

import com.example.simplifyStorePrime.dto.CustomerDTO;
import com.example.simplifyStorePrime.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
    Customer toEntity(CustomerDTO dto);
}
