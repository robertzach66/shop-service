package com.shop.order.controller;

import com.shop.order.dto.OrderRequest;
import com.shop.order.model.Order;
import com.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest orderRequest) {

        try {
            return new ResponseEntity<>("Order: " + orderService.createOrder(orderRequest) + " created successfully!", HttpStatus.CREATED);
        } catch (MissingRequestValueException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity<>("Order could not be created! Reason: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
