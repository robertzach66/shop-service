package com.shop.order.repository;

import com.shop.order.model.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Ordering, Long> {
}
