package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.ProductDTO;
import com.example.simplifyStorePrime.entity.Product;
import com.example.simplifyStorePrime.exception.ErrorMessages;
import com.example.simplifyStorePrime.mapper.ProductMapper;
import com.example.simplifyStorePrime.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public ProductDTO getProductById(Integer id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
    }

    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }

    public ProductDTO updateProduct(Integer id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));

        productMapper.updateEntity(dto, existing);
        Product updated = productRepository.save(existing);

        return productMapper.toDTO(updated);
    }

    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(id);
    }
}