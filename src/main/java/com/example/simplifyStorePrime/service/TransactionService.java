package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.TransactionDTO;
import com.example.simplifyStorePrime.dto.TransactionItemDTO;
import com.example.simplifyStorePrime.entity.Customer;
import com.example.simplifyStorePrime.entity.Product;
import com.example.simplifyStorePrime.entity.Transaction;
import com.example.simplifyStorePrime.entity.TransactionItem;
import com.example.simplifyStorePrime.exception.ErrorMessages;
import com.example.simplifyStorePrime.mapper.TransactionMapper;
import com.example.simplifyStorePrime.repository.CustomerRepository;
import com.example.simplifyStorePrime.repository.ProductRepository;
import com.example.simplifyStorePrime.repository.TransactionItemRepository;
import com.example.simplifyStorePrime.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionDTO createTransaction(TransactionDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND));

        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setCustomer(customer);
        transaction.setTransactionDate(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<TransactionItem> items = new ArrayList<>();

            for (TransactionItemDTO itemDto : dto.getItems()) {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));

                TransactionItem item = new TransactionItem();
                item.setTransaction(savedTransaction);
                item.setProduct(product);
                item.setQuantity(itemDto.getQuantity());
                item.setPricePerUnit(product.getPrice());

                items.add(item);
            }

            transactionItemRepository.saveAll(items);

            savedTransaction.setItems(new ArrayList<>(items));
        }

        return transactionMapper.toDTO(savedTransaction);
    }

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    public TransactionDTO getTransactionById(Integer id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.TRANSACTION_NOT_FOUND));
    }

    public List<TransactionDTO> getTransactionsByCustomerId(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND);
        }
        return transactionRepository.findByCustomerId(customerId).stream()
                .map(transactionMapper::toDTO)
                .toList();
    }
}
