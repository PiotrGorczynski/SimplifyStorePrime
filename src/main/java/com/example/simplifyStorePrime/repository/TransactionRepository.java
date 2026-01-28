package com.example.simplifyStorePrime.repository;

import com.example.simplifyStorePrime.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT DISTINCT t FROM Transaction t " +
            "LEFT JOIN FETCH t.customer " +
            "LEFT JOIN FETCH t.employee " +
            "LEFT JOIN FETCH t.items i " +
            "LEFT JOIN FETCH i.product")
    List<Transaction> findAllWithDetails();

    @Query("SELECT t FROM Transaction t " +
            "LEFT JOIN FETCH t.customer " +
            "LEFT JOIN FETCH t.employee " +
            "LEFT JOIN FETCH t.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE t.id = :id")
    Optional<Transaction> findByIdWithDetails(@Param("id") Integer id);

    List<Transaction> findByCustomerId(Integer customerId);
    List<Transaction> findByTransactionDate(LocalDateTime transactionDate);
    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    List<Transaction> findByStatus(String status);
}
