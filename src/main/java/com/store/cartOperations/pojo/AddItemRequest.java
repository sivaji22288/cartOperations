package com.store.cartOperations.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemRequest {
    @NotNull
    private Integer productId;
    @NotNull
    @Min(1)
    private Integer quantity;
}
