package com.machado.order_processor.controller;

import com.machado.order_processor.factory.OrderResponseFactory;
import com.machado.order_processor.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    OrderService orderService;

    @InjectMocks
    OrderController orderController;

    @Captor
    ArgumentCaptor<Long> customerIdCaptor;

    @Captor
    ArgumentCaptor<PageRequest> pageRequestCaptor;

    private Long customerId;
    private int page;
    private int pageSize;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        page = 0;
        pageSize = 10;
    }

    @Nested
    class ListOrders {

        @Test
        void shouldHandleEmptyPage() {
            doReturn(OrderResponseFactory.buildEmptyPage())
                    .when(orderService).findAllByCustomerId(anyLong(), any());
            doReturn(BigDecimal.ZERO)
                    .when(orderService).findTotalOnOrdersByCustomerId(anyLong());

            var response = orderController.listOrders(customerId, page, pageSize);

            assertNotNull(response.getBody());
            assertTrue(response.getBody().data().isEmpty());
            assertEquals(BigDecimal.ZERO, response.getBody().summary().get("totalOnOrders"));
        }

        @Test
        void shouldHandleCustomerNotFound() {
            doThrow(new RuntimeException("Customer not found"))
                    .when(orderService).findAllByCustomerId(anyLong(), any());

            var exception = assertThrows(RuntimeException.class,
                    () -> orderController.listOrders(customerId, page, pageSize));

            assertEquals("Customer not found", exception.getMessage());
        }

        @Test
        void shouldReturnHttpOk() {
            doReturn(OrderResponseFactory.buildWithOneItem())
                    .when(orderService).findAllByCustomerId(anyLong(), any());
            doReturn(BigDecimal.valueOf(20, 50))
                    .when(orderService).findTotalOnOrdersByCustomerId(anyLong());

            var response = orderController.listOrders(customerId, page, pageSize);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        }

        @Test
        void shouldPassCorrectParametersToService() {
            doReturn(OrderResponseFactory.buildWithOneItem())
                    .when(orderService).findAllByCustomerId(customerIdCaptor.capture(), pageRequestCaptor.capture());
            doReturn(BigDecimal.valueOf(20, 50))
                    .when(orderService).findTotalOnOrdersByCustomerId(customerIdCaptor.capture());

            var response = orderController.listOrders(customerId, page, pageSize);

            assertEquals(2, customerIdCaptor.getAllValues().size());
            assertEquals(customerId, customerIdCaptor.getAllValues().get(0));
            assertEquals(customerId, customerIdCaptor.getAllValues().get(1));
            assertEquals(page, pageRequestCaptor.getValue().getPageNumber());
            assertEquals(pageSize, pageRequestCaptor.getValue().getPageSize());
        }

        @Test
        void shouldReturnResponseBodyCorrectly() {
            var totalOnOrders = BigDecimal.valueOf(20, 50);
            var pagination = OrderResponseFactory.buildWithOneItem();

            doReturn(pagination)
                    .when(orderService).findAllByCustomerId(anyLong(), any());
            doReturn(totalOnOrders)
                    .when(orderService).findTotalOnOrdersByCustomerId(anyLong());

            var response = orderController.listOrders(customerId, page, pageSize);

            assertNotNull(response);
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().data());
            assertNotNull(response.getBody().pagination());
            assertNotNull(response.getBody().summary());

            assertEquals(totalOnOrders, response.getBody().summary().get("totalOnOrders"));
            assertEquals(pagination.getTotalElements(), response.getBody().pagination().totalElements());
            assertEquals(pagination.getTotalPages(), response.getBody().pagination().totalPages());
            assertEquals(pagination.getNumber(), response.getBody().pagination().page());
            assertEquals(pagination.getSize(), response.getBody().pagination().pageSize());

            assertEquals(pagination.getContent(), response.getBody().data());
        }

        @Test
        void shouldHandleMultipleOrders() {
            var multipleOrders = OrderResponseFactory.buildWithMultipleItems(3);
            var totalOnOrders = BigDecimal.valueOf(60, 0);

            doReturn(multipleOrders)
                    .when(orderService).findAllByCustomerId(anyLong(), any());
            doReturn(totalOnOrders)
                    .when(orderService).findTotalOnOrdersByCustomerId(anyLong());

            var response = orderController.listOrders(customerId, page, pageSize);

            assertNotNull(response);
            assertNotNull(response.getBody());
            assertEquals(3, response.getBody().data().size());
            assertEquals(totalOnOrders, response.getBody().summary().get("totalOnOrders"));
        }
    }
}
