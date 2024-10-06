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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
        Customer customer = customerRepository.findByEmail(orderRequest.getCustomer().getEmail());
        if (customer == null) {
            customer = new Customer();
            customer.setCustomerNumber(UUID.randomUUID().toString());
            customer.setEmail(orderRequest.getCustomer().getEmail());
            customer.setFirstName(orderRequest.getCustomer().getFirstName());
            customer.setLastName(orderRequest.getCustomer().getLastName());
        }
        return mapEntityToDto(orderRepository.save(ordering));
    }

    public List<OrderResponse> getOrder(final String email, final String orderNumber) {
        if (orderNumber != null) {
            return List.of(mapEntityToDto(orderRepository.findByOrderNumber(orderNumber)));
        }
        final Customer customer = customerRepository.findByEmail(email);
        return orderRepository.findByCustomerId(customer.getId()).stream().map(this::mapEntityToDto).toList();
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
        OrderResponse orderResponse = OrderResponse.builder()
                .id(ordering.getId())
                .orderNumber(ordering.getOrderNumber())
                .orderDate(ordering.getOrderDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)))
                .orderItems(ordering.getOrderItems().stream()
                        .map(this::mapItemEntityToItemDto)
                        .toList())
                .customer(mapCustomerEntityToDto(ordering.getCustomer()))
                .build();
        orderResponse.setOrderDate(ordering.getOrderDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        return orderResponse;
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
                .email(customer.getEmail())
                .build();
    }
}
