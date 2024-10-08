package com.shop.order.service;

import com.shop.order.client.InventoryClient;
import com.shop.order.dto.*;
import com.shop.order.event.OrderPlacedEvent;
import com.shop.order.model.Customer;
import com.shop.order.model.Ordering;
import com.shop.order.model.OrderItem;
import com.shop.order.repository.CustomerRepository;
import com.shop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingRequestValueException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderDto placeOrder(final OrderDto orderDto) throws MissingRequestValueException {
        boolean allProductsAreInStock = orderDto.orderItems().stream()
                .allMatch(
                        orderItemDto -> {
                            boolean isInStok = inventoryClient.isInStock(orderItemDto.skuCode(), orderItemDto.quantity());
                            log.info("{} {} are {}in stock!", orderItemDto.quantity(), orderItemDto.skuCode(), isInStok ? "" : "not ");
                            return isInStok;
                        }
                );
        if (allProductsAreInStock) {
            return save(orderDto);
        } else {
            throw new IllegalArgumentException("At least one product is not in stock, please try later!");
        }
    }

    public OrderDto createOrder(OrderDto orderDto) throws MissingRequestValueException {
        List<String> skuCodes = orderDto.orderItems()
                .stream()
                .map(OrderItemDto::skuCode)
                .toList();

        List<InventoryDto> inventories = inventoryClient.getInventory(skuCodes);
        boolean allProductsAreInStock = inventories != null && inventories.stream().allMatch(InventoryDto::isInStock);

        if (allProductsAreInStock) {
            return save(orderDto);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try later!");
        }
    }

    private OrderDto save(final OrderDto orderRequest) throws MissingRequestValueException {
        Ordering ordering = new Ordering();
        ordering.setOrderNumber(UUID.randomUUID().toString());
        ordering.setOrderDate(LocalDate.now());
        if (orderRequest.orderItems() == null || orderRequest.orderItems().isEmpty()) {
            throw new MissingRequestValueException("No OrderItems provided!");
        }
        ordering.setOrderItems(orderRequest.orderItems().stream().map(orderItemDto -> mapItemDtoToItemEntity(ordering, orderItemDto)).toList());
        Customer customer = customerRepository.findByEmail(orderRequest.customer().email());
        if (customer == null) {
            customer = new Customer();
            customer.setCustomerNumber(UUID.randomUUID().toString());
            customer.setEmail(orderRequest.customer().email());
            customer.setFirstName(orderRequest.customer().firstName());
            customer.setLastName(orderRequest.customer().lastName());
        }
        ordering.setCustomer(customer);

        OrderDto orderResponse = mapEntityToDto(orderRepository.save(ordering));
        notiFyAboutPlacedOrder(orderResponse);
        return orderResponse;
    }

    private void notiFyAboutPlacedOrder(OrderDto orderDto) {
        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(orderDto.orderNumber(), orderDto.customer().email(), orderDto.customer().firstName(), orderDto.customer().lastName());
        log.info("Notify Topic: order-placed with: {}", orderPlacedEvent);
        CompletableFuture<SendResult<String, OrderPlacedEvent>> sr = kafkaTemplate.send("order-placed", orderPlacedEvent.getOrderNumber(), orderPlacedEvent);
        log.info("Notifyied Topic: order-placed with: {} successfully! SendResult: {}", orderPlacedEvent, sr.toString());
    }

    public List<OrderDto> getOrder(final String email, final String orderNumber) {
        if (orderNumber != null) {
            return List.of(mapEntityToDto(orderRepository.findByOrderNumber(orderNumber)));
        }
        final Customer customer = customerRepository.findByEmail(email);
        return orderRepository.findByCustomerId(customer.getId()).stream().map(this::mapEntityToDto).toList();
    }

    private OrderItem mapItemDtoToItemEntity(Ordering ordering, final OrderItemDto orderItemDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(orderItemDto.quantity());
        orderItem.setPrice(orderItemDto.price());
        orderItem.setSkuCode(orderItemDto.skuCode());
        orderItem.setOrdering(ordering);
        return orderItem;
    }

    private OrderDto mapEntityToDto(final Ordering ordering) {
        return new OrderDto(ordering.getId(),
                ordering.getOrderNumber(),
                ordering.getOrderDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
                ordering.getOrderItems().stream()
                        .map(this::mapItemEntityToItemDto)
                        .toList(),
                mapCustomerEntityToDto(ordering.getCustomer()));
    }

    private OrderItemDto mapItemEntityToItemDto(final OrderItem orderItem) {
        return new OrderItemDto(orderItem.getId(), orderItem.getSkuCode(), orderItem.getPrice(), orderItem.getQuantity());
    }

    private CustomerDto mapCustomerEntityToDto(final Customer customer) {
        return new CustomerDto(customer.getId(), customer.getCustomerNumber(), customer.getFirstName(), customer.getLastName(), customer.getEmail());
    }
}
