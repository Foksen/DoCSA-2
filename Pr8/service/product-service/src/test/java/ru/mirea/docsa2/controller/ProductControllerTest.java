package ru.mirea.docsa2.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.mirea.docsa2.dto.CreateProductRequest;
import ru.mirea.docsa2.dto.ProductResponse;
import ru.mirea.docsa2.dto.UpdateProductRequest;
import ru.mirea.docsa2.model.Product;
import ru.mirea.docsa2.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController productController;

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        Product product = new Product(1L, "Laptop", new BigDecimal("1500.00"), 10);
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> result = productController.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).name());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() {
        Product product = new Product(1L, "Laptop", new BigDecimal("1500.00"), 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<ProductResponse> response = productController.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Laptop", response.getBody().name());
    }

    @Test
    void getProductById_WhenNotExists_ShouldReturn404() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ProductResponse> response = productController.getProductById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        CreateProductRequest request = new CreateProductRequest("Laptop", new BigDecimal("1500.00"), 10);
        Product savedProduct = new Product(1L, "Laptop", new BigDecimal("1500.00"), 10);
        
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ResponseEntity<ProductResponse> response = productController.createProduct(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Laptop", response.getBody().name());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenExists_ShouldReturnUpdatedProduct() {
        UpdateProductRequest request = new UpdateProductRequest("Laptop Pro", new BigDecimal("1800.00"), 5);
        Product existingProduct = new Product(1L, "Laptop", new BigDecimal("1500.00"), 10);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        ResponseEntity<ProductResponse> response = productController.updateProduct(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenNotExists_ShouldReturn404() {
        UpdateProductRequest request = new UpdateProductRequest("Laptop Pro", new BigDecimal("1800.00"), 5);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ProductResponse> response = productController.updateProduct(1L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteProduct_WhenExists_ShouldReturn204() {
        when(productRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = productController.deleteProduct(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_WhenNotExists_ShouldReturn404() {
        when(productRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = productController.deleteProduct(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productRepository, never()).deleteById(any());
    }
}

