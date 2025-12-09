package com.example.simplifyStorePrime.mapper;

import com.example.simplifyStorePrime.dto.ProductDTO;
import com.example.simplifyStorePrime.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
    Product toEntity(ProductDTO dto);
}