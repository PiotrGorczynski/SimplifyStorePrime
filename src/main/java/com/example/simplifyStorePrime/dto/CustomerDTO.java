package com.example.simplifyStorePrime.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Integer id;
    private String info;
    private String salesOrders;
    private String invoices;
    private String paymentHistory;
    private String communication;
    private String category;
    private String feedback;
    private String notes;
    private String supportRequest;
}
