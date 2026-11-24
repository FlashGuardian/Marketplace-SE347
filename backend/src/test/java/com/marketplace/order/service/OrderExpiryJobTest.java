package com.marketplace.order.service;

import com.marketplace.order.model.Order;
import com.marketplace.order.model.OrderStatus;
import com.marketplace.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class OrderExpiryJobTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusService orderStatusService;

    private OrderExpiryJob job;

    @BeforeEach
    void setUp() {
        job = new OrderExpiryJob(orderRepository, orderStatusService);
    }

    @Test
    void cancelsExpiredPendingOrdersThroughSharedServiceAndContinuesAfterFailure() {
        Order first = order(1L);
        Order second = order(2L);
        when(orderRepository.findExpiredPending(eq(OrderStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(first, second));
        doThrow(new InvalidTransitionException()).when(orderStatusService)
                .transition(1L, null, OrderStatus.CANCELLED, "Quá hạn 48 giờ");

        job.cancelExpiredOrders();

        verify(orderStatusService).transition(1L, null, OrderStatus.CANCELLED, "Quá hạn 48 giờ");
        verify(orderStatusService).transition(2L, null, OrderStatus.CANCELLED, "Quá hạn 48 giờ");
        verify(orderRepository).findExpiredPending(eq(OrderStatus.PENDING), any(LocalDateTime.class));
    }

    @Test
    void leavesOrdersAloneWhenNoPendingOrdersHaveExpired() {
        when(orderRepository.findExpiredPending(eq(OrderStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of());

        job.cancelExpiredOrders();

        verify(orderStatusService, never()).transition(any(), any(), any(), any());
    }

    private Order order(Long id) {
        Order order = org.mockito.Mockito.mock(Order.class);
        lenient().when(order.getId()).thenReturn(id);
        return order;
    }
}
