package com.shop.order.repository;

import com.shop.order.model.Customer;
import com.shop.order.model.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Ordering, Long> {

    Ordering findByOrderNumber(String orderNumber);
    List<Ordering> findByCustomerId(Long customerId);
}
