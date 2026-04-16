package com.pollinate.service;

import com.pollinate.dto.CreateProductRequest;
import com.pollinate.dto.ProductResponse;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> searchProducts(String name, Boolean inStock, int page, int size);
    ProductResponse updateStock(Long id, int stockQuantity);
}

