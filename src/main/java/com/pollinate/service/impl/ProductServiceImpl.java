package com.pollinate.service.impl;

import com.pollinate.dto.CreateProductRequest;
import com.pollinate.dto.ProductResponse;
import com.pollinate.exception.ResourceNotFoundException;
import com.pollinate.mapper.ProductMapper;
import com.pollinate.model.Product;
import com.pollinate.repository.ProductRepository;
import com.pollinate.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Instant now = Instant.now();
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        Product saved = productRepository.save(product);
        log.info("Product {} ('{}') created with stock={}", saved.getId(), saved.getName(), saved.getStockQuantity());
        return ProductMapper.toResponse(saved);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getId))
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse updateStock(Long id, int stockQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        int previous = product.getStockQuantity();
        product.setStockQuantity(stockQuantity);
        product.setUpdatedAt(Instant.now());
        log.info("Product {} stock updated: {} → {}", id, previous, stockQuantity);
        return ProductMapper.toResponse(productRepository.save(product));
    }
}

