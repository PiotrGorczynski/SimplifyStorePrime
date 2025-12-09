package com.example.simplifyStorePrime.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "delivery")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String deliveryType;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String provider;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    @ToString.Exclude
    private Transaction transaction;
}
