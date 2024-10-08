package com.shop.order.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ORDER_ITEMS", schema = "SHOP")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_items_generator")
    @SequenceGenerator(name = "order_items_generator", sequenceName = "shop.ORDER_ITEMS_ID_SEQ", allocationSize = 1)
    private Long id;

    private String skuCode;
    private BigDecimal price;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Ordering ordering;
}
