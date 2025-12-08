package com.example.simplifyStorePrime.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "transaction_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "price_per_unit", nullable = false)
    private Double pricePerUnit;
}