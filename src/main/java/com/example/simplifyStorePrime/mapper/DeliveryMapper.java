package com.example.simplifyStorePrime.mapper;

import com.example.simplifyStorePrime.dto.DeliveryDTO;
import com.example.simplifyStorePrime.entity.Delivery;
import com.example.simplifyStorePrime.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    @Mapping(source = "transaction.id", target = "transactionId")
    DeliveryDTO toDTO(Delivery delivery);

    @Mapping(target = "transaction", ignore = true)
    Delivery toEntity(DeliveryDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transaction", source = "tr")
    @Mapping(target = "status", source = "dto.status")
    @Mapping(target = "provider", source = "dto.provider")
    void updateEntity(@MappingTarget Delivery delivery, DeliveryDTO dto, Transaction tr);
}
