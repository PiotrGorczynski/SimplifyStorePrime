package com.example.simplifyStorePrime.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryDTO {
    private Integer id;
    private String deliveryType;
    private String status;
    private String provider;
    private Integer transactionId;
}
