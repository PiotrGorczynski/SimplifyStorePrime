package com.example.simplifyStorePrime.repository;

import com.example.simplifyStorePrime.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByCustomerId(Integer customerId);

    List<Transaction> findByTransactionDate(LocalDateTime transactionDate);

    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    List<Transaction> findByStatus(String status);
}
