package com.pollinate.repository;

import com.pollinate.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
}

