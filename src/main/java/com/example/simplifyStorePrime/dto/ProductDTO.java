package com.example.simplifyStorePrime.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {
    private Integer id;
    private String name;
    private String code;
    private String category;
    private Double price;
    private Integer stock;
    private String description;
    private String notes;
    private Integer minQuantity;
    private String another;
}
