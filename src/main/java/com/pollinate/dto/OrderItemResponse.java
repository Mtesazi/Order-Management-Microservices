package com.pollinate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product name", example = "Laptop")
    private String productName;

    @Schema(description = "Product unit price at purchase time", example = "1200.00")
    private BigDecimal unitPrice;

    @Schema(description = "Ordered quantity", example = "2")
    private int quantity;

    @Schema(description = "Line total amount", example = "2400.00")
    private BigDecimal lineTotal;
}
