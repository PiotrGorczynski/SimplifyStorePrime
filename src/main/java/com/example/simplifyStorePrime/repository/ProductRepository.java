package com.example.simplifyStorePrime.repository;

import com.example.simplifyStorePrime.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByCode(String code);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :phrase, '%')) OR " +
            "LOWER(p.code) LIKE LOWER(CONCAT('%', :phrase, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :phrase, '%'))")
    List<Product> searchByPhrase(String phrase);
}
