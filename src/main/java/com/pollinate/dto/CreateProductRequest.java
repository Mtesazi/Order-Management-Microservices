package com.pollinate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    @Schema(description = "Product name", example = "Laptop")
    @NotBlank
    private String name;

    @Schema(description = "Product description", example = "14-inch business laptop")
    private String description;

    @Schema(description = "Unit price", example = "1200.00")
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @Schema(description = "Available stock units", example = "5")
    @Min(0)
    private int stockQuantity;
}
