package com.store.cartOperations.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue
    Integer id;
    @Builder.Default
    Double totalCost=0.0;
    @Builder.Default
    Double billDiscount=0.0;
    @Builder.Default
    Double userDiscount=0.0;
    @NotNull
    @OneToOne
    RetailUser retailUser;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart", fetch = FetchType.EAGER)
    List<Item> items;
}
