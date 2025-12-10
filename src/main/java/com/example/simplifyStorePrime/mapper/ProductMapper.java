package com.example.simplifyStorePrime.mapper;

import com.example.simplifyStorePrime.dto.ProductDTO;
import com.example.simplifyStorePrime.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);

    Product toEntity(ProductDTO dto);

    void updateEntity(ProductDTO dto, @MappingTarget Product entity);
}