package com.pollinate.service.impl;

import com.pollinate.dto.CreateProductRequest;
import com.pollinate.dto.ProductResponse;
import com.pollinate.exception.ResourceNotFoundException;
import com.pollinate.model.Product;
import com.pollinate.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProductShouldTrimNameAndPersist() {
        CreateProductRequest request = new CreateProductRequest(
                "  Laptop  ",
                "Business laptop",
                new BigDecimal("1200.00"),
                5
        );

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(1L);
            return product;
        });

        ProductResponse response = productService.createProduct(request);

        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals(new BigDecimal("1200.00"), response.getPrice());
        assertEquals(5, response.getStockQuantity());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProductByIdShouldThrowWhenMissing() {
        when(productRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(7L));
    }

    @Test
    void getAllProductsShouldReturnSortedById() {
        Product second = new Product(2L, "Mouse", "Wireless", new BigDecimal("25.00"), 10, Instant.now(), Instant.now());
        Product first = new Product(1L, "Laptop", "Business", new BigDecimal("1200.00"), 5, Instant.now(), Instant.now());

        when(productRepository.findAll()).thenReturn(List.of(second, first));

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
    }

    @Test
    void updateStockShouldPersistNewValue() {
        Product product = new Product(1L, "Laptop", "Business", new BigDecimal("1200.00"), 5, Instant.now(), Instant.now());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.updateStock(1L, 20);

        assertEquals(20, response.getStockQuantity());
        verify(productRepository).save(product);
    }
}

