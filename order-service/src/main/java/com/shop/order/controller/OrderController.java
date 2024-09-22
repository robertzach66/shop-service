package com.shop.order.controller;

import com.shop.order.dto.OrderRequest;
import com.shop.order.dto.OrderResponse;
import com.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {

        try {
            return new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatus.CREATED);
        } catch (IllegalArgumentException | MissingRequestValueException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (WebClientResponseException e) {
            if (e.getRequest() != null) {
                log.error("The following uri could not been answerd successfully:");
                log.error("URI: {}", e.getRequest().getURI());
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {

        try {
            return new ResponseEntity<>(orderService.createOrder(orderRequest), HttpStatus.CREATED);
        } catch (IllegalArgumentException | MissingRequestValueException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (WebClientResponseException e) {
            if (e.getRequest() != null) {
                log.error("The following uri could not been answerd successfully:");
                log.error("URI: {}", e.getRequest().getURI());
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
