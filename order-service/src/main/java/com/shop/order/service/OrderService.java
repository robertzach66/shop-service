package com.shop.order.service;

import com.shop.order.dto.InventoryResponse;
import com.shop.order.dto.OrderItemDto;
import com.shop.order.dto.OrderRequest;
import com.shop.order.model.Order;
import com.shop.order.model.OrderItem;
import com.shop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public String createOrder(OrderRequest orderRequest) throws MissingRequestValueException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new MissingRequestValueException("No OrderItems provided!");
        }
        order.setOrderItems(orderRequest.getOrderItems().stream().map(orderItemDto -> mapDtoToEntity(order, orderItemDto)).toList());

        List<String> skuCodes = orderRequest.getOrderItems()
                .stream()
                .map(OrderItemDto::getSkuCode)
                .toList();

        InventoryResponse[] inventories = webClient.get()
                .uri("http://localhost:8082/api/inventory/",
                        uriBuilder -> uriBuilder.queryParam("sku-codes", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsAreInStock = inventories != null && Arrays.stream(inventories).allMatch(InventoryResponse::isInStock);

        if (allProductsAreInStock) {
            return orderRepository.save(order).getOrderNumber();
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try later!");
        }
    }

    public OrderItem mapDtoToEntity(Order order, final OrderItemDto orderItemDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPrice(orderItemDto.getPrice());
        orderItem.setSkuCode(orderItemDto.getSkuCode());
        orderItem.setOrder(order);
        return orderItem;
    }
}
