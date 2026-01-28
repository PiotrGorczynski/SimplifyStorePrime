package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.TransactionDTO;
import com.example.simplifyStorePrime.dto.TransactionItemDTO;
import com.example.simplifyStorePrime.entity.*;
import com.example.simplifyStorePrime.mapper.TransactionMapper;
import com.example.simplifyStorePrime.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final DeliveryRepository deliveryRepository;
    private final TransactionMapper transactionMapper;
    private final AppUserRepository appUserRepository;

    @Transactional
    public TransactionDTO createTransaction(TransactionDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(CUSTOMER_NOT_FOUND));

        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setCustomer(customer);
        String username = getCurrentUsername();
        if (username != null) {
            appUserRepository.findByUsername(username)
                    .ifPresent(transaction::setEmployee);
        }
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

        if (requiresDelivery(dto.getType())) {
            createDeliveryForTransaction(savedTransaction, dto.getProvider());
        }

        return transactionMapper.toDTO(savedTransaction);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    private boolean requiresDelivery(String type) {
        return SALE.equalsIgnoreCase(type) || EXCHANGE.equalsIgnoreCase(type);
    }

    private void createDeliveryForTransaction(Transaction transaction, String provider) {
        Delivery delivery = new Delivery();
        delivery.setTransaction(transaction);
        delivery.setDeliveryType("standard");
        delivery.setStatus("pending");
        delivery.setProvider(provider != null ? provider : "Default Provider");

        deliveryRepository.save(delivery);
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAllWithDetails().stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Integer id) {
        return transactionRepository.findByIdWithDetails(id)
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
        Transaction existing = transactionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException(TRANSACTION_NOT_FOUND));

        String oldType = existing.getType();
        String newType = dto.getType() != null ? dto.getType() : oldType;
        boolean typeChanged = !oldType.equalsIgnoreCase(newType);

        transactionMapper.updateEntity(dto, existing);

        String username = getCurrentUsername();
        if (username != null) {
            appUserRepository.findByUsername(username)
                    .ifPresent(existing::setEmployee);
        }

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
        } else if (typeChanged) {
            for (TransactionItem item : existing.getItems()) {
                Product product = item.getProduct();
                updateStock(product, item.getQuantity(), oldType, true);
                updateStock(product, item.getQuantity(), newType, false);
            }
        }

        if (typeChanged) {
            handleDeliveryOnTypeChange(existing, oldType, newType, dto.getProvider());
        }

        Transaction saved = transactionRepository.save(existing);
        return transactionMapper.toDTO(saved);
    }

    private void handleDeliveryOnTypeChange(Transaction transaction, String oldType, String newType, String provider) {
        boolean hadDelivery = requiresDelivery(oldType);
        boolean needsDelivery = requiresDelivery(newType);

        if (hadDelivery && !needsDelivery) {
            deliveryRepository.deleteByTransactionId(transaction.getId());
        } else if (!hadDelivery && needsDelivery) {
            if (!deliveryRepository.existsByTransactionId(transaction.getId())) {
                createDeliveryForTransaction(transaction, provider);
            }
        }
    }

    @Transactional
    public void delete(Integer id) {
        Transaction transaction = transactionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException(TRANSACTION_NOT_FOUND));

        if (transaction.getItems() != null) {
            for (TransactionItem item : transaction.getItems()) {
                Product product = item.getProduct();
                updateStock(product, item.getQuantity(), transaction.getType(), true);
            }
        }

        deliveryRepository.deleteByTransactionId(id);

        transactionRepository.deleteById(id);
    }

    private void updateStock(Product product, int quantity, String type, boolean revert) {
        int currentStock = product.getStock();
        int newStock;

        boolean isSale = SALE.equalsIgnoreCase(type);
        boolean isReturn = RETURN.equalsIgnoreCase(type);
        boolean isExchange = EXCHANGE.equalsIgnoreCase(type);
        boolean isRefund = REFUND.equalsIgnoreCase(type);

        if (isExchange || isRefund) {
            return;
        }

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