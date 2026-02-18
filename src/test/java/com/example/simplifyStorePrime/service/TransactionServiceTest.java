package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.TransactionDTO;
import com.example.simplifyStorePrime.entity.*;
import com.example.simplifyStorePrime.mapper.TransactionMapper;
import com.example.simplifyStorePrime.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionItemRepository transactionItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Transaction sampleTransaction() {
        return Transaction.builder()
                .id(1)
                .transactionDate(LocalDateTime.now())
                .type("sale")
                .total(299.99)
                .paymentMethod("card")
                .status("completed")
                .provider("DPD")
                .items(new ArrayList<>())
                .build();
    }

    private TransactionDTO sampleDTO() {
        return TransactionDTO.builder()
                .id(1)
                .customerId(1)
                .type("sale")
                .total(299.99)
                .paymentMethod("card")
                .status("completed")
                .provider("DPD")
                .build();
    }

    @Test
    void getAllTransactions_shouldReturnList() {
        Transaction transaction = sampleTransaction();
        TransactionDTO dto = sampleDTO();

        when(transactionRepository.findAllWithDetails()).thenReturn(List.of(transaction));
        when(transactionMapper.toDTO(transaction)).thenReturn(dto);

        List<TransactionDTO> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
        assertEquals("sale", result.get(0).getType());
        verify(transactionRepository).findAllWithDetails();
    }

    @Test
    void getAllTransactions_emptyDatabase_shouldReturnEmptyList() {
        when(transactionRepository.findAllWithDetails()).thenReturn(List.of());

        List<TransactionDTO> result = transactionService.getAllTransactions();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionById_existingId_shouldReturnTransaction() {
        Transaction transaction = sampleTransaction();
        TransactionDTO dto = sampleDTO();

        when(transactionRepository.findByIdWithDetails(1)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDTO(transaction)).thenReturn(dto);

        TransactionDTO result = transactionService.getTransactionById(1);

        assertNotNull(result);
        assertEquals(299.99, result.getTotal());
        assertEquals("card", result.getPaymentMethod());
    }

    @Test
    void getTransactionById_nonExistingId_shouldThrowException() {
        when(transactionRepository.findByIdWithDetails(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getTransactionById(999));
    }

    @Test
    void getTransactionsByCustomerId_existingCustomer_shouldReturnList() {
        Transaction transaction = sampleTransaction();
        TransactionDTO dto = sampleDTO();

        when(customerRepository.existsById(1)).thenReturn(true);
        when(transactionRepository.findByCustomerId(1)).thenReturn(List.of(transaction));
        when(transactionMapper.toDTO(transaction)).thenReturn(dto);

        List<TransactionDTO> result = transactionService.getTransactionsByCustomerId(1);

        assertEquals(1, result.size());
    }

    @Test
    void getTransactionsByCustomerId_nonExistingCustomer_shouldThrowException() {
        when(customerRepository.existsById(999)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getTransactionsByCustomerId(999));
    }

    @Test
    void delete_existingTransaction_shouldDeleteAndRestoreStock() {
        Product product = Product.builder()
                .id(1).name("Laptop").stock(45).price(2999.99).build();

        TransactionItem item = TransactionItem.builder()
                .id(1).product(product).quantity(5).pricePerUnit(2999.99).build();

        Transaction transaction = sampleTransaction();
        transaction.setType("sale");
        transaction.setItems(List.of(item));

        when(transactionRepository.findByIdWithDetails(1)).thenReturn(Optional.of(transaction));

        transactionService.delete(1);

        assertEquals(50, product.getStock());
        verify(transactionRepository).deleteById(1);
        verify(deliveryRepository).deleteByTransactionId(1);
    }

    @Test
    void delete_nonExistingTransaction_shouldThrowException() {
        when(transactionRepository.findByIdWithDetails(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> transactionService.delete(999));
    }

    @Test
    void createTransaction_sale_shouldDecreaseStock() {
        Customer customer = Customer.builder().id(1).info("Test").build();
        Product product = Product.builder()
                .id(1).name("Phone").stock(100).price(999.99).build();

        TransactionDTO dto = sampleDTO();
        dto.setType("sale");
        dto.setItems(List.of(
                com.example.simplifyStorePrime.dto.TransactionItemDTO.builder()
                        .productId(1).quantity(3).build()
        ));

        Transaction savedTransaction = sampleTransaction();
        savedTransaction.setItems(new ArrayList<>());

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(transactionMapper.toEntity(dto)).thenReturn(savedTransaction);
        when(transactionRepository.save(any())).thenReturn(savedTransaction);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(transactionMapper.toDTO(any())).thenReturn(dto);

        transactionService.createTransaction(dto);

        assertEquals(97, product.getStock());
        verify(productRepository).save(product);
    }

    @Test
    void createTransaction_sale_insufficientStock_shouldThrowException() {
        Customer customer = Customer.builder().id(1).info("Test").build();
        Product product = Product.builder()
                .id(1).name("Phone").stock(2).price(999.99).build();

        TransactionDTO dto = sampleDTO();
        dto.setType("sale");
        dto.setItems(List.of(
                com.example.simplifyStorePrime.dto.TransactionItemDTO.builder()
                        .productId(1).quantity(10).build()
        ));

        Transaction savedTransaction = sampleTransaction();
        savedTransaction.setItems(new ArrayList<>());

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(transactionMapper.toEntity(dto)).thenReturn(savedTransaction);
        when(transactionRepository.save(any())).thenReturn(savedTransaction);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> transactionService.createTransaction(dto));
    }
}