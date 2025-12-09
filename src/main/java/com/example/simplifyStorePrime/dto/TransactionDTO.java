package com.example.simplifyStorePrime.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TransactionDTO {
    private Integer id;
    private LocalDate date;
    private String type;
    private Double total;
    private String paymentMethod;
    private String status;
    private String provider;
    private Integer customerId;
    private String customerInfo;
    private String employeeName;
    private List<TransactionItemDTO> items;
}