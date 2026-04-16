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
import java.util.Locale;

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
    public List<ProductResponse> searchProducts(String name, Boolean inStock, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size > 0 ? size : 10;

        String normalizedName = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
        boolean hasNameFilter = !normalizedName.isEmpty();

        List<Product> filteredProducts = productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getId))
                .filter(product -> !hasNameFilter
                        || (product.getName() != null
                        && product.getName().toLowerCase(Locale.ROOT).contains(normalizedName)))
                .filter(product -> inStock == null
                        || (inStock ? product.getStockQuantity() > 0 : product.getStockQuantity() == 0))
                .toList();

        long startLong = (long) normalizedPage * normalizedSize;
        if (startLong >= filteredProducts.size()) {
            return List.of();
        }

        int start = (int) startLong;
        int end = Math.min(start + normalizedSize, filteredProducts.size());

        return filteredProducts.subList(start, end).stream()
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

