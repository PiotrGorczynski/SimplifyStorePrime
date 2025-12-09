package com.example.simplifyStorePrime.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "customer")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String info;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String salesOrders;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String invoices;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String paymentHistory;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String communication;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedback;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String supportRequest;
}
