package com.machado.order_processor.listener.dto;

import java.util.List;

public record OrderCreatedEvent(Long orderCode,
                                Long clientCode,
                                List<OrderItemEvent> items) {
}
