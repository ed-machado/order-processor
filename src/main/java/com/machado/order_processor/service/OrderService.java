package com.machado.order_processor.service;

import com.machado.order_processor.entity.OrderEntity;
import com.machado.order_processor.entity.OrderItem;
import com.machado.order_processor.listener.dto.OrderCreatedEvent;
import org.springframework.stereotype.Service;
import com.machado.order_processor.repository.OrderRepository;


import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void save(OrderCreatedEvent event) {
        var entity = new OrderEntity();
        entity.setCustomerId(event.clientCode());
        entity.setOrderId(event.orderCode());
        entity.setTotalPrice(getTotal(event));
        entity.setOrderItemList(getOrderItems(event));

        orderRepository.save(entity);
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.items().stream().map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity()))).
                reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    private static List<OrderItem> getOrderItems(OrderCreatedEvent event) {
        return event.items().stream()
                .map(i -> new OrderItem(i.product(), i.quantity(), i.price()))
                .toList();
    }
}
