package com.pollinate.service.impl;

import com.pollinate.dto.CreateOrderItemRequest;
import com.pollinate.dto.CreateOrderRequest;
import com.pollinate.dto.OrderResponse;
import com.pollinate.exception.InsufficientStockException;
import com.pollinate.exception.ResourceNotFoundException;
import com.pollinate.model.Order;
import com.pollinate.model.OrderItem;
import com.pollinate.model.OrderStatus;
import com.pollinate.model.Product;
import com.pollinate.repository.OrderRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrderShouldCalculateTotalAndUpdateStock() {
        Product product = new Product(
                1L,
                "Laptop",
                "Business laptop",
                new BigDecimal("1200.00"),
                5,
                Instant.now(),
                Instant.now()
        );

        CreateOrderItemRequest item = new CreateOrderItemRequest(1L, 2);
        CreateOrderRequest request = new CreateOrderRequest(List.of(item));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(10L);
            return order;
        });

        OrderResponse response = orderService.createOrder(request);

        assertEquals(10L, response.getId());
        assertEquals(OrderStatus.CREATED, response.getStatus());
        assertEquals(new BigDecimal("2400.00"), response.getTotalAmount());
        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getItems().getFirst().getQuantity());
        assertEquals(3, product.getStockQuantity());

        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrderShouldRejectWhenProductMissing() {
        CreateOrderItemRequest item = new CreateOrderItemRequest(99L, 1);
        CreateOrderRequest request = new CreateOrderRequest(List.of(item));

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createOrderShouldRejectWhenStockInsufficient() {
        Product product = new Product(
                1L,
                "Laptop",
                "Business laptop",
                new BigDecimal("1200.00"),
                1,
                Instant.now(),
                Instant.now()
        );

        CreateOrderItemRequest item = new CreateOrderItemRequest(1L, 2);
        CreateOrderRequest request = new CreateOrderRequest(List.of(item));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createOrderShouldNotMutateStockWhenAnyProductIsMissing() {
        Product existing = new Product(
                1L,
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("90.00"),
                5,
                Instant.now(),
                Instant.now()
        );

        CreateOrderRequest request = new CreateOrderRequest(List.of(
                new CreateOrderItemRequest(1L, 2),
                new CreateOrderItemRequest(999L, 1)
        ));

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(request));
        assertEquals(5, existing.getStockQuantity());
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void searchOrdersShouldFilterByIdWhenProvided() {
        Order first = buildOrder(1L, "Laptop");
        Order second = buildOrder(2L, "Keyboard");
        when(orderRepository.findAll()).thenReturn(List.of(first, second));

        List<OrderResponse> result = orderService.searchOrdersByIdAndProductName(2L, null, 0, 10);

        assertEquals(1, result.size());
        assertEquals(2L, result.getFirst().getId());
    }

    @Test
    void searchOrdersShouldFilterByProductNameIgnoringCase() {
        Order first = buildOrder(1L, "Laptop Pro");
        Order second = buildOrder(2L, "Mechanical Keyboard");
        when(orderRepository.findAll()).thenReturn(List.of(first, second));

        List<OrderResponse> result = orderService.searchOrdersByIdAndProductName(null, "  laptop  ", 0, 10);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }

    @Test
    void searchOrdersShouldApplyPaginationAndHandleOutOfRangePage() {
        Order first = buildOrder(1L, "A");
        Order second = buildOrder(2L, "B");
        Order third = buildOrder(3L, "C");
        when(orderRepository.findAll()).thenReturn(List.of(third, first, second));

        List<OrderResponse> pageOne = orderService.searchOrdersByIdAndProductName(null, null, 1, 2);
        List<OrderResponse> outOfRange = orderService.searchOrdersByIdAndProductName(null, null, 5, 2);

        assertEquals(1, pageOne.size());
        assertEquals(3L, pageOne.getFirst().getId());
        assertEquals(0, outOfRange.size());
    }

    private static Order buildOrder(Long id, String productName) {
        OrderItem item = new OrderItem(
                id,
                id,
                productName,
                new BigDecimal("10.00"),
                1,
                new BigDecimal("10.00")
        );
        return new Order(
                id,
                List.of(item),
                OrderStatus.CREATED,
                new BigDecimal("10.00"),
                Instant.now()
        );
    }
}

