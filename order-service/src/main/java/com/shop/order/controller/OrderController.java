package com.shop.order.controller;

import com.shop.order.dto.OrderDto;
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
    public ResponseEntity<OrderDto> placeOrder(@RequestBody OrderDto orderDto) {

        try {
            log.info("Start to place order: {}", orderDto);
            final OrderDto orderResponse = orderService.placeOrder(orderDto);
            log.info("Order: {} placed successfully!", orderResponse);
            return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
        } catch (MissingRequestValueException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {

        try {
            return new ResponseEntity<>(orderService.createOrder(orderDto), HttpStatus.CREATED);
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
    public ResponseEntity<List<OrderDto>> getOrderByEmail(@RequestParam(value = "email", required = false) String email, @RequestParam(value = "order-number", required = false) String orderNumber) {
        log.info("Start to get order: {}", email);
        List<OrderDto> orderResponses = orderService.getOrder(email, orderNumber);
        log.info("Found {} Orders!", orderResponses.size());
        return new ResponseEntity<>(orderResponses, HttpStatus.OK);
    }
}
