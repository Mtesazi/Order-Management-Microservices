package com.pollinate.service;

import com.pollinate.dto.CreateOrderRequest;
import com.pollinate.dto.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> searchOrdersByIdAndProductName(Long id, String productName, int page, int size);
}
