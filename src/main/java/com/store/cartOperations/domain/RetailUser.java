package com.store.cartOperations.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RetailUser {

    //Generate documentation for the class
    /**
     * This class represents a user of the store.
     * A user can be an employee or an affiliated user.
     * A user can have a cart.
     */

    @Id
    @GeneratedValue
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @Builder.Default
    private boolean isEmployee=false;
    @NotNull
    @Builder.Default
    private boolean isAffiliated=false;
    @NotNull
    private Date registeredOn;
}
