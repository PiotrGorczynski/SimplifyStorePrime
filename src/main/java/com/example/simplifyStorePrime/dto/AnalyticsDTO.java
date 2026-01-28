package com.example.simplifyStorePrime.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    private Double totalSales;
    private Long totalCustomers;
    private Long totalProducts;
    private Long totalDeliveries;

    private Map<String, Long> paymentMethodsDistribution;

    private Map<String, Long> transactionStatusDistribution;

    private List<ProductRevenueDTO> topProducts;

    private List<DailyTransactionDTO> dailyTransactions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRevenueDTO {
        private String productName;
        private Double revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyTransactionDTO {
        private String date;
        private Long count;
        private Double revenue;
    }
}