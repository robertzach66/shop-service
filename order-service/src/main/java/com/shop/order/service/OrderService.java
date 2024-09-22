package com.shop.order.service;

import com.shop.order.dto.*;
import com.shop.order.model.Ordering;
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
    private final WebClient.Builder webClientBuilder;

    public OrderResponse createOrder(OrderRequest orderRequest) throws MissingRequestValueException {
        List<String> skuCodes = orderRequest.getOrderItems()
                .stream()
                .map(OrderItemRequest::getSkuCode)
                .toList();

        InventoryResponse[] inventories = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("sku-code", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsAreInStock = inventories != null && Arrays.stream(inventories).allMatch(InventoryResponse::isInStock);

        if (allProductsAreInStock) {
            Ordering ordering = new Ordering();
            ordering.setOrderNumber(UUID.randomUUID().toString());
            if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
                throw new MissingRequestValueException("No OrderItems provided!");
            }
            ordering.setOrderItems(orderRequest.getOrderItems().stream().map(orderItemRequest -> mapItemDtoToItemEntity(ordering, orderItemRequest)).toList());
            return mapEntityToDto(orderRepository.save(ordering));
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try later!");
        }
    }

    private OrderItem mapItemDtoToItemEntity(Ordering ordering, final OrderItemRequest orderItemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setPrice(orderItemRequest.getPrice());
        orderItem.setSkuCode(orderItemRequest.getSkuCode());
        orderItem.setOrdering(ordering);
        return orderItem;
    }

    private OrderResponse mapEntityToDto(Ordering ordering) {
        return OrderResponse.builder()
                .id(ordering.getId())
                .orderNumber(ordering.getOrderNumber())
                .orderItems(ordering.getOrderItems().stream()
                        .map(this::mapItemEntityToItemDto)
                        .toList())
                .build();
    }

    private OrderItemResponse mapItemEntityToItemDto(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .skuCode(orderItem.getSkuCode())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
