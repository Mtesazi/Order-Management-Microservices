package com.pollinate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    @Schema(description = "Product ID", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Laptop")
    private String name;

    @Schema(description = "Product description", example = "14-inch business laptop")
    private String description;

    @Schema(description = "Unit price", example = "1200.00")
    private BigDecimal price;

    @Schema(description = "Available stock units", example = "5")
    private int stockQuantity;

    @Schema(description = "Product creation timestamp", example = "2026-04-09T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Product last update timestamp", example = "2026-04-09T10:20:30Z")
    private Instant updatedAt;
}
