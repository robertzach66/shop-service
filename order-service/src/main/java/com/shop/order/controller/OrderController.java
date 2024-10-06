package com.shop.order.controller;

import com.shop.order.dto.OrderRequest;
import com.shop.order.dto.OrderResponse;
import com.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {

        try {
            log.info("Start to place order: {}", orderRequest);
            final OrderResponse orderResponse = orderService.placeOrder(orderRequest);
            log.info("Order: {} placed successfully!", orderResponse);
            return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException | MissingRequestValueException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (WebClientResponseException e) {
            if (e.getRequest() != null) {
                log.error("The following uri could not been answerd successfully:");
                log.error("URI: {}", e.getRequest().getURI());
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Failed to  place order, because: {}", e.getMessage(), e);
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

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderByEmail(@RequestParam(value = "email", required = false) String email, @RequestParam(value = "order-number", required = false) String orderNumber) {
        log.info("Start to get order: {}", email);
        List<OrderResponse> orderResponses = orderService.getOrder(email, orderNumber);
        log.info("Found {} Orders!", orderResponses.size());
        return new ResponseEntity<>(orderResponses, HttpStatus.OK);
    }
}
