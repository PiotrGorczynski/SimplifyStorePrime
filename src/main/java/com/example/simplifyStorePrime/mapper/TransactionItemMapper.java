package com.example.simplifyStorePrime.mapper;

import com.example.simplifyStorePrime.dto.TransactionItemDTO;
import com.example.simplifyStorePrime.entity.TransactionItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionItemMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.code", target = "productCode")
    TransactionItemDTO toDTO(TransactionItem item);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    TransactionItem toEntity(TransactionItemDTO dto);
}
