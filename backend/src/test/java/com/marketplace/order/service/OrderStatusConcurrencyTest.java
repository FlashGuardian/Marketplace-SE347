package com.marketplace.order.service;

import com.marketplace.auth.model.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.order.model.Order;
import com.marketplace.order.model.OrderStatus;
import com.marketplace.order.repository.OrderRepository;
import com.marketplace.order.repository.OrderStatusHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("mysql")
@SpringBootTest
@ActiveProfiles("mysql-it")
class OrderStatusConcurrencyTest {

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusHistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long orderId;
    private Long buyerId;
    private Long sellerId;

    @AfterEach
    void cleanUp() {
        if (orderId != null) {
            orderRepository.deleteById(orderId);
        }
        if (buyerId != null) {
            userRepository.deleteById(buyerId);
        }
        if (sellerId != null) {
            userRepository.deleteById(sellerId);
        }
    }

    @Test
    void onlyOneConcurrentTransitionIsApplied() throws Exception {
        User buyer = userRepository.saveAndFlush(new User(
                "Cart concurrency buyer", "cart-buyer-" + System.nanoTime() + "@test.local",
                "08" + System.nanoTime() % 1_000_000_000_000L, "hash"
        ));
        User seller = userRepository.saveAndFlush(new User(
                "Cart concurrency seller", "cart-seller-" + System.nanoTime() + "@test.local",
                "09" + System.nanoTime() % 1_000_000_000_000L, "hash"
        ));
        buyerId = buyer.getId();
        sellerId = seller.getId();
        Order order = orderRepository.saveAndFlush(new Order(
                "R" + UUID.randomUUID().toString().replace("-", "").substring(0, 19),
                buyerId, sellerId, "Receiver", "0900000000",
                "Address", Order.DeliveryMethod.MEET, BigDecimal.TEN, LocalDateTime.now().plusDays(2)
        ));
        orderId = order.getId();

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> sellerTransition = executor.submit(() -> tryTransition(
                    ready, start, sellerId, OrderStatus.CONFIRMED
            ));
            Future<Boolean> rejectTransition = executor.submit(() -> tryTransition(
                    ready, start, sellerId, OrderStatus.REJECTED
            ));
            ready.await();
            start.countDown();
            List<Boolean> results = List.of(sellerTransition.get(), rejectTransition.get());
            assertEquals(1, results.stream().filter(Boolean::booleanValue).count());
            assertEquals(1L, historyRepository.countByOrderId(orderId));
        } finally {
            executor.shutdownNow();
        }
    }

    private boolean tryTransition(
            CountDownLatch ready,
            CountDownLatch start,
            Long actorId,
            OrderStatus to
    ) throws InterruptedException {
        ready.countDown();
        start.await();
        try {
            orderStatusService.transition(
                    orderId, actorId, to,
                    to == OrderStatus.CANCELLED || to == OrderStatus.REJECTED ? "Concurrent decision" : null
            );
            return true;
        } catch (InvalidTransitionException exception) {
            return false;
        }
    }
}
