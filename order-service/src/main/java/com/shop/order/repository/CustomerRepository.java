package com.shop.order.repository;

import com.shop.order.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByCustomerNumber(String customerNumber);
    Customer findByEmail(String email);
}
