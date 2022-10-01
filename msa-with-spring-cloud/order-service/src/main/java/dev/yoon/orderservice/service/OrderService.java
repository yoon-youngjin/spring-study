package dev.yoon.orderservice.service;

import dev.yoon.orderservice.dto.OrderDto;
import dev.yoon.orderservice.repository.OrderEntity;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDetails);

    OrderDto getOrderByOrderId(String orderId);

    List<OrderEntity> getOrdersByUserId(String userId);
}
