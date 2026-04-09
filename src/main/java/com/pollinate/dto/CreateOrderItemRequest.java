package com.pollinate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequest {
    @Schema(description = "Product ID to order", example = "1")
    @NotNull
    private Long productId;

    @Schema(description = "Quantity for this product", example = "2")
    @Min(1)
    private int quantity;
}
