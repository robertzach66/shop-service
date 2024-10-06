package com.shop.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ORDERS", schema = "SHOP")
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_generator")
    @SequenceGenerator(name = "orders_generator", sequenceName = "shop.ORDERS_ID_SEQ", allocationSize = 1)
    private Long id;

    @NotNull
    private String orderNumber;
    @NotNull
    private LocalDate orderDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ordering")
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
