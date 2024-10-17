package com.machado.order_processor.listener.dto;

import java.util.List;

public record OrderCreatedEvent(Long product_id, Long customer_id, List<OrderItemEvent> items) {
}
