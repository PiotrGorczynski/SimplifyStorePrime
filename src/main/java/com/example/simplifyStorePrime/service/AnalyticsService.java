package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.AnalyticsDTO;
import com.example.simplifyStorePrime.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final DeliveryRepository deliveryRepository;
    private final TransactionItemRepository transactionItemRepository;

    @Transactional(readOnly = true)
    public AnalyticsDTO getSummary() {
        return AnalyticsDTO.builder()
                .totalSales(calculateTotalSales())
                .totalCustomers(customerRepository.count())
                .totalProducts(productRepository.count())
                .totalDeliveries(deliveryRepository.count())
                .paymentMethodsDistribution(getPaymentMethodsDistribution())
                .transactionStatusDistribution(getTransactionStatusDistribution())
                .topProducts(getTopProductsByRevenue())
                .dailyTransactions(getDailyTransactions())
                .build();
    }

    private Double calculateTotalSales() {
        return transactionRepository.findAll().stream()
                .filter(t -> "sale".equalsIgnoreCase(t.getType()))
                .mapToDouble(t -> t.getTotal() != null ? t.getTotal() : 0.0)
                .sum();
    }

    private Map<String, Long> getPaymentMethodsDistribution() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getPaymentMethod() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getPaymentMethod(),
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getTransactionStatusDistribution() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getStatus(),
                        Collectors.counting()
                ));
    }

    private List<AnalyticsDTO.ProductRevenueDTO> getTopProductsByRevenue() {
        Map<String, Double> productRevenue = new HashMap<>();

        transactionItemRepository.findAll().forEach(item -> {
            if (item.getProduct() != null) {
                String productName = item.getProduct().getName();
                double revenue = item.getQuantity() * item.getPricePerUnit();
                productRevenue.merge(productName, revenue, Double::sum);
            }
        });

        return productRevenue.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .map(e -> AnalyticsDTO.ProductRevenueDTO.builder()
                        .productName(e.getKey())
                        .revenue(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<AnalyticsDTO.DailyTransactionDTO> getDailyTransactions() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        Map<LocalDate, List<com.example.simplifyStorePrime.entity.Transaction>> grouped =
                transactionRepository.findAll().stream()
                        .filter(t -> t.getTransactionDate() != null && t.getTransactionDate().isAfter(sevenDaysAgo))
                        .collect(Collectors.groupingBy(t -> t.getTransactionDate().toLocalDate()));

        List<AnalyticsDTO.DailyTransactionDTO> result = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<com.example.simplifyStorePrime.entity.Transaction> dayTransactions =
                    grouped.getOrDefault(date, Collections.emptyList());

            result.add(AnalyticsDTO.DailyTransactionDTO.builder()
                    .date(date.toString())
                    .count((long) dayTransactions.size())
                    .revenue(dayTransactions.stream()
                            .mapToDouble(t -> t.getTotal() != null ? t.getTotal() : 0.0)
                            .sum())
                    .build());
        }

        return result;
    }
}