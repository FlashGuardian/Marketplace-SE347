package com.marketplace.order.service;

import com.marketplace.order.model.Order;
import com.marketplace.order.model.OrderStatus;
import com.marketplace.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderExpiryJob {

    private static final Logger logger = LoggerFactory.getLogger(OrderExpiryJob.class);

    private final OrderRepository orderRepository;
    private final OrderStatusService orderStatusService;

    public OrderExpiryJob(OrderRepository orderRepository, OrderStatusService orderStatusService) {
        this.orderRepository = orderRepository;
        this.orderStatusService = orderStatusService;
    }

    @Scheduled(fixedDelayString = "${app.order-expiry.fixed-delay-ms:60000}")
    public void cancelExpiredOrders() {
        for (Order order : orderRepository.findExpiredPending(OrderStatus.PENDING, LocalDateTime.now())) {
            try {
                orderStatusService.transition(
                        order.getId(), null, OrderStatus.CANCELLED, "Quá hạn 48 giờ"
                );
            } catch (RuntimeException exception) {
                logger.warn("Could not auto-cancel expired order {}", order.getId(), exception);
            }
        }
    }
}
