package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.DeliveryDTO;
import com.example.simplifyStorePrime.entity.Delivery;
import com.example.simplifyStorePrime.entity.Transaction;
import com.example.simplifyStorePrime.exception.ErrorMessages;
import com.example.simplifyStorePrime.mapper.DeliveryMapper;
import com.example.simplifyStorePrime.repository.DeliveryRepository;
import com.example.simplifyStorePrime.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final TransactionRepository transactionRepository;
    private final DeliveryMapper deliveryMapper;

    public List<DeliveryDTO> getAllDeliveries() {
        return deliveryRepository.findAll().stream()
                .map(deliveryMapper::toDTO)
                .toList();
    }

    public DeliveryDTO getDeliveryById(Integer id) {
        return deliveryRepository.findById(id)
                .map(deliveryMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.DELIVERY_NOT_FOUND));
    }

    public DeliveryDTO createDelivery(DeliveryDTO dto) {
        Transaction transaction = transactionRepository.findById(dto.getTransactionId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.TRANSACTION_NOT_FOUND));

        if (deliveryRepository.findByTransactionId(dto.getTransactionId()).isPresent()) {
            throw new IllegalStateException(ErrorMessages.DELIVERY_ALREADY_EXISTS);
        }

        Delivery delivery = deliveryMapper.toEntity(dto);
        delivery.setTransaction(transaction);

        Delivery saved = deliveryRepository.save(delivery);
        return deliveryMapper.toDTO(saved);
    }

    public DeliveryDTO updateDelivery(Integer id, DeliveryDTO dto) {
        Delivery existing = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.DELIVERY_NOT_FOUND));

        Transaction transaction = transactionRepository.findById(dto.getTransactionId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.TRANSACTION_NOT_FOUND));

        deliveryMapper.updateEntity(existing, dto, transaction);

        Delivery updated = deliveryRepository.save(existing);
        return deliveryMapper.toDTO(updated);
    }

    public void deleteDelivery(Integer id) {
        if (!deliveryRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorMessages.DELIVERY_NOT_FOUND);
        }
        deliveryRepository.deleteById(id);
    }
}