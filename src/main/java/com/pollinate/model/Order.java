package com.pollinate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Instant createdAt;
}
