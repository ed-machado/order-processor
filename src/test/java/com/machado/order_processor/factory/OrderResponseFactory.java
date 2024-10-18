package com.machado.order_processor.factory;

import com.machado.order_processor.controller.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderResponseFactory {

    public static Page<OrderResponse> buildWithOneItem() {
        var orderResponse = new OrderResponse(1L, 2L, BigDecimal.valueOf(20,50));
        return new PageImpl<>(List.of(orderResponse));
    }

    public static Page<OrderResponse> buildWithMultipleItems(int itemCount) {
        List<OrderResponse> orders = IntStream.range(0, itemCount)
                .mapToObj(i -> new OrderResponse((long) i, 2L, BigDecimal.valueOf(20, 50)))
                .collect(Collectors.toList());
        return new PageImpl<>(orders);
    }

    public static Page<OrderResponse> buildEmptyPage() {
        return new PageImpl<>(List.of());
    }

}
