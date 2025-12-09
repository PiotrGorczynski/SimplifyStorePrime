package com.example.simplifyStorePrime.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionItemDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productCode;
    private Integer quantity;
    private Double pricePerUnit;
}
