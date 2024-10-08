package com.shop.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PRODUCTS", schema = "SHOP")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_generator")
    @SequenceGenerator(name = "product_generator", sequenceName = "shop.products_ID_seq", allocationSize = 1)
    private Long id;
    @NotNull
    private String skuCode;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private BigDecimal price;
}
