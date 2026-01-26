package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.TransactionDTO;
import com.example.simplifyStorePrime.dto.TransactionItemDTO;
import com.example.simplifyStorePrime.entity.Customer;
import com.example.simplifyStorePrime.entity.Product;
import com.example.simplifyStorePrime.entity.Transaction;
import com.example.simplifyStorePrime.entity.TransactionItem;
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

import static com.example.simplifyStorePrime.exception.ErrorMessages.*;

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
                .orElseThrow(() -> new EntityNotFoundException(CUSTOMER_NOT_FOUND));

        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setCustomer(customer);
        transaction.setTransactionDate(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<TransactionItem> items = new ArrayList<>();

            for (TransactionItemDTO itemDto : dto.getItems()) {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_FOUND));

                updateStock(product, itemDto.getQuantity(), dto.getType(), false);

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

    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Integer id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(TRANSACTION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByCustomerId(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException(CUSTOMER_NOT_FOUND);
        }
        return transactionRepository.findByCustomerId(customerId).stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    @Transactional
    public TransactionDTO update(Integer id, TransactionDTO dto) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TRANSACTION_NOT_FOUND));

        String oldType = existing.getType();

        transactionMapper.updateEntity(dto, existing);

        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException(CUSTOMER_NOT_FOUND));
            existing.setCustomer(customer);
        }

        if (dto.getItems() != null) {
            for (TransactionItem oldItem : existing.getItems()) {
                Product product = oldItem.getProduct();
                updateStock(product, oldItem.getQuantity(), oldType, true);
            }

            existing.getItems().clear();
            transactionItemRepository.deleteAll(
                    transactionItemRepository.findByTransactionId(id)
            );

            String newType = dto.getType() != null ? dto.getType() : oldType;
            for (TransactionItemDTO itemDto : dto.getItems()) {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_FOUND));

                updateStock(product, itemDto.getQuantity(), newType, false);

                TransactionItem item = new TransactionItem();
                item.setTransaction(existing);
                item.setProduct(product);
                item.setQuantity(itemDto.getQuantity());
                item.setPricePerUnit(itemDto.getPricePerUnit() != null
                        ? itemDto.getPricePerUnit()
                        : product.getPrice());

                existing.getItems().add(item);
            }
        }

        Transaction saved = transactionRepository.save(existing);
        return transactionMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TRANSACTION_NOT_FOUND));

        for (TransactionItem item : transaction.getItems()) {
            Product product = item.getProduct();
            updateStock(product, item.getQuantity(), transaction.getType(), true);
        }

        transactionRepository.deleteById(id);
    }

    private void updateStock(Product product, int quantity, String type, boolean revert) {
        int currentStock = product.getStock();
        int newStock;

        boolean isSale = SALE.equalsIgnoreCase(type);
        boolean isReturn = RETURN.equalsIgnoreCase(type);

        if (revert) {
            if (isSale) {
                newStock = currentStock + quantity;
            } else if (isReturn) {
                newStock = currentStock - quantity;
            } else {
                return;
            }
        } else {
            if (isSale) {
                newStock = currentStock - quantity;
                if (newStock < 0) {
                    throw new IllegalStateException(NOT_ENOUGH_STOCK + product.getName());
                }
            } else if (isReturn) {
                newStock = currentStock + quantity;
            } else {
                return;
            }
        }

        product.setStock(newStock);
        productRepository.save(product);
    }
}