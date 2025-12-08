package com.example.simplifyStorePrime.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = true)
    private Double total;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false, length = 100)
    private String provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<TransactionItem> items = new ArrayList<>();
}