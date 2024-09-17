package com.shop.order.service;

import com.shop.order.dto.OrderItemDto;
import com.shop.order.dto.OrderRequest;
import com.shop.order.model.Order;
import com.shop.order.model.OrderItem;
import com.shop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingRequestValueException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    final OrderRepository orderRepository;

    public String createOrder(OrderRequest orderRequest) throws MissingRequestValueException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new MissingRequestValueException("No OrderItems provided!");
        }
        order.setOrderItems(orderRequest.getOrderItems().stream().map(orderItemDto -> mapDtoToEntity(order, orderItemDto)).toList());
        return orderRepository.save(order).getOrderNumber();
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
