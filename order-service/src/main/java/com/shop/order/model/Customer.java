package com.shop.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CUSTOMERS", schema = "SHOP")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_generator")
    @SequenceGenerator(name = "customers_generator", sequenceName = "shop.CUSTOMS_ID_SEQ", allocationSize = 1)
    private Long id;

    @NotNull
    private String customerNumber;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
}
