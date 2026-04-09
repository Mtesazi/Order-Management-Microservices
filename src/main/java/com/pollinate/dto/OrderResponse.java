package com.pollinate.dto;

import com.pollinate.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Order line items")
    private List<OrderItemResponse> items;

    @Schema(description = "Current order status", example = "CREATED")
    private OrderStatus status;

    @Schema(description = "Order total amount", example = "2400.00")
    private BigDecimal totalAmount;

    @Schema(description = "Order creation timestamp", example = "2026-04-09T10:25:30Z")
    private Instant createdAt;
}
