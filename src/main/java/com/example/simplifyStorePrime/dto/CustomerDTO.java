package com.example.simplifyStorePrime.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
