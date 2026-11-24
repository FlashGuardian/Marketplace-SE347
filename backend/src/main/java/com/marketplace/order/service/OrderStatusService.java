package com.marketplace.order.service;

import com.marketplace.order.model.Order;
import com.marketplace.order.model.OrderStatus;
import com.marketplace.order.model.OrderStatusHistory;
import com.marketplace.order.repository.OrderRepository;
import com.marketplace.order.repository.OrderStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;

    public OrderStatusService(OrderRepository orderRepository, OrderStatusHistoryRepository historyRepository) {
        this.orderRepository = orderRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public OrderStatusHistory transition(Long orderId, Long actorUserId, OrderStatus to, String reason) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        OrderStatus from = order.getStatus();

        if (!from.canTransitionTo(to)) {
            throw new InvalidTransitionException();
        }
        validateActor(order, from, to, actorUserId);
        if ((to == OrderStatus.CANCELLED || to == OrderStatus.REJECTED)
                && (reason == null || reason.isBlank())) {
            throw new ReasonRequiredException();
        }

        LocalDateTime changedAt = LocalDateTime.now();
        order.transitionTo(to, actorUserId, reason, changedAt);
        OrderStatusHistory history = new OrderStatusHistory(
                orderId, from, to, actorUserId, reason, changedAt
        );
        return historyRepository.save(history);
    }

    private void validateActor(Order order, OrderStatus from, OrderStatus to, Long actorUserId) {
        boolean allowed = switch (from) {
            case PENDING -> {
                if (to == OrderStatus.CONFIRMED || to == OrderStatus.REJECTED) {
                    yield actorUserId != null && actorUserId.equals(order.getSellerId());
                }
                yield actorUserId == null || actorUserId.equals(order.getBuyerId());
            }
            case CONFIRMED -> actorUserId != null
                    && (actorUserId.equals(order.getBuyerId()) || actorUserId.equals(order.getSellerId()));
            case SHIPPING -> actorUserId != null && actorUserId.equals(order.getBuyerId());
            case COMPLETED, REJECTED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new ActorNotAllowedException();
        }
    }
}
