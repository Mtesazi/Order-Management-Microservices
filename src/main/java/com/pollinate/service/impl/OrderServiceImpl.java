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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order with {} item(s)", request.getItems().size());
        List<RequestedProduct> requestedProducts = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderItemRequest requestItem : request.getItems()) {
            Product product = productRepository.findById(requestItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + requestItem.getProductId()));

            requestedProducts.add(new RequestedProduct(product, requestItem.getQuantity()));
        }

        for (RequestedProduct requested : requestedProducts) {
            Product product = requested.product();
            int quantity = requested.quantity();

            if (product.getStockQuantity() < quantity) {
                log.warn("Insufficient stock for product {}: requested={}, available={}",
                        product.getId(), quantity, product.getStockQuantity());
                throw new InsufficientStockException("Insufficient stock for product " + product.getId());
            }

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(lineTotal);

            OrderItem orderItem = new OrderItem(
                    null,
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity,
                    lineTotal
            );
            orderItems.add(orderItem);
        }

        for (RequestedProduct requested : requestedProducts) {
            Product product = requested.product();
            product.setStockQuantity(product.getStockQuantity() - requested.quantity());
            product.setUpdatedAt(Instant.now());
            productRepository.save(product);
            log.debug("Deducted {} unit(s) from product {} (remaining: {})",
                    requested.quantity(), product.getId(), product.getStockQuantity());
        }

        Order order = new Order();
        order.setItems(orderItems);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(Instant.now());

        Order saved = orderRepository.save(order);
        log.info("Order {} created – {} item(s), total {}", saved.getId(), orderItems.size(), totalAmount);
        return OrderMapper.toResponse(saved);
    }

    private record RequestedProduct(Product product, int quantity) {
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
