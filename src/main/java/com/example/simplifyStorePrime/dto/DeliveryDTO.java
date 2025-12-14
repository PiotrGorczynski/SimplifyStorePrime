package com.example.simplifyStorePrime.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {
    private Integer id;
    private String deliveryType;
    private String status;
    private String provider;
    private Integer transactionId;
}
