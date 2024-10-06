package com.shop.order.service;

import com.shop.order.client.InventoryClient;
import com.shop.order.dto.*;
import com.shop.order.model.Customer;
import com.shop.order.model.Ordering;
import com.shop.order.model.OrderItem;
import com.shop.order.repository.CustomerRepository;
import com.shop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingRequestValueException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final InventoryClient inventoryClient;

    public OrderResponse placeOrder(final OrderRequest orderRequest) throws MissingRequestValueException {
        boolean allProductsAreInStock = orderRequest.getOrderItems().stream()
                .allMatch(
                        orderItemRequest -> {
                            boolean isInStok = inventoryClient.isInStock(orderItemRequest.getSkuCode(), orderItemRequest.getQuantity());
                            log.info("{} {} are {}in stock!", orderItemRequest.getQuantity(), orderItemRequest.getSkuCode(), isInStok ? "" : "not ");
                            return isInStok;
                        }
                );
        if (allProductsAreInStock) {
            return save(orderRequest);
        } else {
            throw new IllegalArgumentException("At least one product is not in stock, please try later!");
        }
    }

    public OrderResponse createOrder(OrderRequest orderRequest) throws MissingRequestValueException {
        List<String> skuCodes = orderRequest.getOrderItems()
                .stream()
                .map(OrderItemRequest::getSkuCode)
                .toList();

        List<InventoryResponse> inventories = inventoryClient.getInventory(skuCodes);
        boolean allProductsAreInStock = inventories != null && inventories.stream().allMatch(InventoryResponse::isInStock);

        if (allProductsAreInStock) {
            return save(orderRequest);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try later!");
        }
    }

    private OrderResponse save(final OrderRequest orderRequest) throws MissingRequestValueException {
        Ordering ordering = new Ordering();
        ordering.setOrderNumber(UUID.randomUUID().toString());
        ordering.setOrderDate(LocalDate.now());
        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new MissingRequestValueException("No OrderItems provided!");
        }
        ordering.setOrderItems(orderRequest.getOrderItems().stream().map(orderItemRequest -> mapItemDtoToItemEntity(ordering, orderItemRequest)).toList());
        Customer customer = customerRepository.findByEmail(orderRequest.getCustomerRequest().getEmail());
        if (customer == null) {
            customer = new Customer();
            customer.setCustomerNumber(UUID.randomUUID().toString());
            customer.setEmail(orderRequest.getCustomerRequest().getEmail());
            customer.setFirstName(orderRequest.getCustomerRequest().getFirstName());
            customer.setLastName(orderRequest.getCustomerRequest().getLastName());
        }
        return mapEntityToDto(orderRepository.save(ordering));
    }

    public OrderResponse getOrder(final String orderNumber) {
        return mapEntityToDto(orderRepository.findByOrderNumber(orderNumber));
    }

    private OrderItem mapItemDtoToItemEntity(Ordering ordering, final OrderItemRequest orderItemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setPrice(orderItemRequest.getPrice());
        orderItem.setSkuCode(orderItemRequest.getSkuCode());
        orderItem.setOrdering(ordering);
        return orderItem;
    }

    private OrderResponse mapEntityToDto(final Ordering ordering) {
        return OrderResponse.builder()
                .id(ordering.getId())
                .orderNumber(ordering.getOrderNumber())
                .orderItems(ordering.getOrderItems().stream()
                        .map(this::mapItemEntityToItemDto)
                        .toList())
                .customerResponse(mapCustomerEntityToDto(ordering.getCustomer()))
                .build();
    }

    private OrderItemResponse mapItemEntityToItemDto(final OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .skuCode(orderItem.getSkuCode())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }

    private CustomerResponse mapCustomerEntityToDto(final Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerNumber(customer.getCustomerNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .build();
    }
}
