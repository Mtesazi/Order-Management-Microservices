package com.pollinate.mapper;

import com.pollinate.dto.OrderItemResponse;
import com.pollinate.dto.OrderResponse;
import com.pollinate.model.Order;
import com.pollinate.model.OrderItem;

import java.util.List;

public final class OrderMapper {
    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(mapItems(order.getItems()));
        return response;
    }

    private static List<OrderItemResponse> mapItems(List<OrderItem> items) {
        return items.stream().map(item -> {
            OrderItemResponse responseItem = new OrderItemResponse();
            responseItem.setProductId(item.getProductId());
            responseItem.setProductName(item.getProductName());
            responseItem.setUnitPrice(item.getUnitPrice());
            responseItem.setQuantity(item.getQuantity());
            responseItem.setLineTotal(item.getLineTotal());
            return responseItem;
        }).toList();
    }
}

