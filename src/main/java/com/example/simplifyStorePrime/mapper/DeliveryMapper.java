package com.example.simplifyStorePrime.mapper;

import com.example.simplifyStorePrime.dto.DeliveryDTO;
import com.example.simplifyStorePrime.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    @Mapping(source = "transaction.id", target = "transactionId")
    DeliveryDTO toDTO(Delivery delivery);

    @Mapping(target = "transaction", ignore = true)
    Delivery toEntity(DeliveryDTO dto);
}
