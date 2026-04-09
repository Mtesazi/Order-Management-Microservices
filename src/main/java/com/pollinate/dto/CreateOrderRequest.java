package com.pollinate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @Schema(description = "Items included in the order", example = "[{\"productId\":1,\"quantity\":2}]")
    @Valid
    @NotEmpty
    private List<CreateOrderItemRequest> items;
}
