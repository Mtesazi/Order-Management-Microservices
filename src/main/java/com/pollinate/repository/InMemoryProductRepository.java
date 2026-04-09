package com.pollinate.repository;

import com.pollinate.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryProductRepository implements ProductRepository {
    private final ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Product save(Product product) {
        Objects.requireNonNull(product, "product must not be null");

        Long productId = product.getId();
        if (productId == null) {
            productId = idGenerator.incrementAndGet();
            product.setId(productId);
        }

        products.put(productId, product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}

