package com.pollinate.service.impl;

import com.pollinate.dto.CreateOrderItemRequest;
import com.pollinate.dto.CreateOrderRequest;
import com.pollinate.dto.OrderResponse;
import com.pollinate.exception.InsufficientStockException;
import com.pollinate.exception.ResourceNotFoundException;
import com.pollinate.mapper.OrderMapper;
import com.pollinate.model.Order;
import com.pollinate.model.OrderItem;
import com.pollinate.model.OrderStatus;
import com.pollinate.model.Product;
import com.pollinate.repository.OrderRepository;
import com.pollinate.repository.ProductRepository;
import com.pollinate.service.OrderService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderItemRequest requestItem : request.getItems()) {
            Product product = productRepository.findById(requestItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + requestItem.getProductId()));

            if (product.getStockQuantity() < requestItem.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product " + product.getId());
            }

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(requestItem.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    requestItem.getQuantity(),
                    lineTotal
            );
            orderItems.add(orderItem);

            product.setStockQuantity(product.getStockQuantity() - requestItem.getQuantity());
            product.setUpdatedAt(Instant.now());
            productRepository.save(product);
        }

        Order order = new Order();
        order.setItems(orderItems);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(Instant.now());

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getId))
                .map(OrderMapper::toResponse)
                .toList();
    }
}
