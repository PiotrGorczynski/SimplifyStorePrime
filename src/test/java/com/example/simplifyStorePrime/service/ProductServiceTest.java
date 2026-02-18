package com.example.simplifyStorePrime.service;

import com.example.simplifyStorePrime.dto.ProductDTO;
import com.example.simplifyStorePrime.entity.Product;
import com.example.simplifyStorePrime.mapper.ProductMapper;
import com.example.simplifyStorePrime.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct() {
        return Product.builder()
                .id(1)
                .name("Laptop Dell XPS 15")
                .code("DELL-XPS-15")
                .category("electronics")
                .price(5999.99)
                .stock(50)
                .description("High-end laptop")
                .notes("Bestseller")
                .minQuantity(5)
                .another(null)
                .build();
    }

    private ProductDTO sampleDTO() {
        return ProductDTO.builder()
                .id(1)
                .name("Laptop Dell XPS 15")
                .code("DELL-XPS-15")
                .category("electronics")
                .price(5999.99)
                .stock(50)
                .description("High-end laptop")
                .notes("Bestseller")
                .minQuantity(5)
                .another(null)
                .build();
    }

    @Test
    void getAllProducts_shouldReturnList() {
        Product product = sampleProduct();
        ProductDTO dto = sampleDTO();

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toDTO(product)).thenReturn(dto);

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Laptop Dell XPS 15", result.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void getAllProducts_emptyDatabase_shouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductDTO> result = productService.getAllProducts();

        assertTrue(result.isEmpty());
    }

    @Test
    void getProductById_existingId_shouldReturnProduct() {
        Product product = sampleProduct();
        ProductDTO dto = sampleDTO();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(dto);

        ProductDTO result = productService.getProductById(1);

        assertNotNull(result);
        assertEquals("DELL-XPS-15", result.getCode());
        assertEquals(5999.99, result.getPrice());
    }

    @Test
    void getProductById_nonExistingId_shouldThrowException() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> productService.getProductById(999));
    }

    @Test
    void createProduct_validData_shouldReturnCreated() {
        Product product = sampleProduct();
        ProductDTO dto = sampleDTO();

        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(dto);

        ProductDTO result = productService.createProduct(dto);

        assertNotNull(result);
        assertEquals("Laptop Dell XPS 15", result.getName());
        assertEquals(50, result.getStock());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_existingId_shouldReturnUpdated() {
        Product existing = sampleProduct();
        ProductDTO dto = sampleDTO();
        dto.setPrice(4999.99);

        when(productRepository.findById(1)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);
        when(productMapper.toDTO(existing)).thenReturn(dto);

        ProductDTO result = productService.updateProduct(1, dto);

        assertEquals(4999.99, result.getPrice());
        verify(productMapper).updateEntity(dto, existing);
    }

    @Test
    void updateProduct_nonExistingId_shouldThrowException() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> productService.updateProduct(999, sampleDTO()));
    }

    @Test
    void deleteProduct_existingId_shouldDelete() {
        when(productRepository.existsById(1)).thenReturn(true);

        productService.deleteProduct(1);

        verify(productRepository).deleteById(1);
    }

    @Test
    void deleteProduct_nonExistingId_shouldThrowException() {
        when(productRepository.existsById(999)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> productService.deleteProduct(999));
        verify(productRepository, never()).deleteById(999);
    }
}