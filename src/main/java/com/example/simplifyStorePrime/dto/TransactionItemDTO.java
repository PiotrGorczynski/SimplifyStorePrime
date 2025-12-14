package com.example.simplifyStorePrime.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionItemDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productCode;
    private Integer quantity;
    private Double pricePerUnit;
}
