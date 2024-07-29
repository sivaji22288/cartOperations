package com.store.cartOperations.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    Integer id;
    @NotNull
    String name;
    String description;
    @NotNull
    Category category;
    @NotNull
    Double price;
    @Builder.Default
    Integer availableQuantity=0;
    @Builder.Default
    Boolean isEmployeeDiscountEnabled=false;

}
