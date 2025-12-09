package com.example.simplifyStorePrime.mapper;

import com.example.simplifyStorePrime.dto.TransactionDTO;
import com.example.simplifyStorePrime.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TransactionItemMapper.class})
public interface TransactionMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.info", target = "customerInfo")
    @Mapping(source = "employee.username", target = "employeeName")
    TransactionDTO toDTO(Transaction transaction);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "items", ignore = true)
    Transaction toEntity(TransactionDTO dto);
}
