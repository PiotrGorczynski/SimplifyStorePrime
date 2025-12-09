package com.example.simplifyStorePrime.repository;

import com.example.simplifyStorePrime.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.info) LIKE LOWER(CONCAT('%', :phrase, '%')) OR " +
            "LOWER(c.communication) LIKE LOWER(CONCAT('%', :phrase, '%')) OR " +
            "LOWER(c.category) LIKE LOWER(CONCAT('%', :phrase, '%')) OR " +
            "LOWER(c.salesOrders) LIKE LOWER(CONCAT('%', :phrase, '%'))")
    List<Customer> searchByPhrase(String phrase);
}
