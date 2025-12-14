package com.example.simplifyStorePrime.mapper;

import com.example.simplifyStorePrime.dto.TransactionDTO;
import com.example.simplifyStorePrime.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TransactionItemMapper.class})
public interface TransactionMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.info", target = "customerInfo")
    @Mapping(source = "employee.username", target = "employeeName")
    @Mapping(source = "transactionDate", target = "date")
    TransactionDTO toDTO(Transaction transaction);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(source = "date", target = "transactionDate")
    Transaction toEntity(TransactionDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(source = "date", target = "transactionDate")
    void updateEntity(TransactionDTO dto, @MappingTarget Transaction entity);
}
