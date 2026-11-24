package com.marketplace.order.service;

import com.marketplace.order.model.Order;
import com.marketplace.order.model.OrderStatus;
import com.marketplace.order.model.OrderStatusHistory;
import com.marketplace.order.repository.OrderRepository;
import com.marketplace.order.repository.OrderStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusHistoryRepository historyRepository;

    private OrderStatusService service;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new OrderStatusService(orderRepository, historyRepository);
        order = new Order(
                "test-order", 10L, 20L, "Receiver", "0900000000", "Address",
                Order.DeliveryMethod.MEET, BigDecimal.TEN, LocalDateTime.now().plusDays(2)
        );
        when(orderRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(order));
    }

    @Test
    void acceptsEachDefinedTransitionWithItsActorAndWritesHistory() {
        when(historyRepository.save(any(OrderStatusHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertTransition(OrderStatus.CONFIRMED, 20L, null);
        assertEquals(LocalDateTime.now().getYear(), order.getConfirmedAt().getYear());
        assertTransition(OrderStatus.SHIPPING, 20L, null);
        assertTransition(OrderStatus.COMPLETED, 10L, null);
        assertNotNull(order.getCompletedAt());

        order = newOrder();
        when(orderRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(order));
        assertTransition(2L, OrderStatus.REJECTED, 20L, "Không thể giao");
        assertEquals("Không thể giao", order.getRejectReason());

        order = newOrder();
        when(orderRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(order));
        assertTransition(3L, OrderStatus.CANCELLED, 10L, "Đổi ý");
        assertEquals(Order.CancelSource.BUYER, order.getCancelSource());

        order = newOrder();
        when(orderRepository.findByIdForUpdate(4L)).thenReturn(Optional.of(order));
        assertTransition(4L, OrderStatus.CANCELLED, null, "Quá hạn");
        assertEquals(Order.CancelSource.SYSTEM, order.getCancelSource());

        order = newOrder();
        when(orderRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(order));
        assertTransition(5L, OrderStatus.CONFIRMED, 20L, null);
        assertTransition(5L, OrderStatus.CANCELLED, 10L, "Buyer cancel");
        assertEquals(Order.CancelSource.BUYER, order.getCancelSource());

        order = newOrder();
        when(orderRepository.findByIdForUpdate(6L)).thenReturn(Optional.of(order));
        assertTransition(6L, OrderStatus.CONFIRMED, 20L, null);
        assertTransition(6L, OrderStatus.CANCELLED, 20L, "Seller cancel");
        assertEquals(Order.CancelSource.SELLER, order.getCancelSource());
    }

    @Test
    void rejectsInvalidTransitionsActorsAndMissingReasonsWithoutHistory() {
        assertThrows(ReasonRequiredException.class, () -> service.transition(
                1L, 20L, OrderStatus.REJECTED, "  "
        ));
        assertThrows(ActorNotAllowedException.class, () -> service.transition(
                1L, 10L, OrderStatus.CONFIRMED, null
        ));
        assertThrows(InvalidTransitionException.class, () -> service.transition(
                1L, 20L, OrderStatus.COMPLETED, null
        ));
        verify(historyRepository, never()).save(any(OrderStatusHistory.class));

        when(historyRepository.save(any(OrderStatusHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Order rejectedOrder = newOrder();
        when(orderRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(rejectedOrder));
        service.transition(2L, 20L, OrderStatus.REJECTED, "Cannot fulfill");
        assertThrows(InvalidTransitionException.class, () -> service.transition(
                2L, 20L, OrderStatus.CANCELLED, "Already terminal"
        ));
        verify(historyRepository, times(1)).save(any(OrderStatusHistory.class));
    }

    @Test
    void recordsOneAppendOnlyHistoryEntryWithActorAndTimestamp() {
        when(historyRepository.save(any(OrderStatusHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderStatusHistory history = service.transition(1L, 20L, OrderStatus.CONFIRMED, null);

        ArgumentCaptor<OrderStatusHistory> captor = ArgumentCaptor.forClass(OrderStatusHistory.class);
        verify(historyRepository).save(captor.capture());
        assertEquals(OrderStatus.PENDING, captor.getValue().getFromStatus());
        assertEquals(OrderStatus.CONFIRMED, history.getToStatus());
        assertEquals(20L, history.getChangedBy());
        assertNotNull(history.getCreatedAt());
    }

    private void assertTransition(OrderStatus to, Long actor, String reason) {
        assertTransition(1L, to, actor, reason);
    }

    private void assertTransition(Long orderId, OrderStatus to, Long actor, String reason) {
        OrderStatusHistory history = service.transition(orderId, actor, to, reason);
        assertEquals(to, history.getToStatus());
    }

    private Order newOrder() {
        return new Order(
                "test-order-" + System.nanoTime(), 10L, 20L, "Receiver", "0900000000", "Address",
                Order.DeliveryMethod.MEET, BigDecimal.TEN, LocalDateTime.now().plusDays(2)
        );
    }
}
