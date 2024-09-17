package com.shop.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "INVENTORIES")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventories_generator")
    @SequenceGenerator(name = "inventories_generator", sequenceName = "INVENTORIES_ID_SEQ", allocationSize = 1)
    private Long id;
    private String skuCode;
    private Integer quantity;
}
