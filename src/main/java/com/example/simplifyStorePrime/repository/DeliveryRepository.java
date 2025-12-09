package com.example.simplifyStorePrime.repository;

import com.example.simplifyStorePrime.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    Optional<Delivery> findByTransactionId(Integer transactionId);
}
