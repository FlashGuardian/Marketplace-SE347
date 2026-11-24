package com.marketplace.order.repository;

import com.marketplace.order.model.OrderStatusHistory;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OrderStatusHistoryRepository extends Repository<OrderStatusHistory, Long> {

    OrderStatusHistory save(OrderStatusHistory history);

    List<OrderStatusHistory> findByOrderIdOrderByCreatedAtAscIdAsc(Long orderId);

    long countByOrderId(Long orderId);
}
