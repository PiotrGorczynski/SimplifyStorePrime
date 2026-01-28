package com.example.simplifyStorePrime.repository;

import com.example.simplifyStorePrime.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    List<Delivery> findByStatus(String status);

    Optional<Delivery> findByTransactionId(Integer transactionId);

    boolean existsByTransactionId(Integer transactionId);

    void deleteByTransactionId(Integer transactionId);
}
