package com.example.simplifyStorePrime.repository;

import com.example.simplifyStorePrime.entity.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItem, Integer> {
    List<TransactionItem> findByTransactionId(Integer transactionId);
}
