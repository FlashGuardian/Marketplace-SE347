package com.marketplace.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_code", nullable = false, unique = true, length = 20)
    private String orderCode;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 15)
    private String receiverPhone;

    @Column(name = "receiver_address", nullable = false, length = 255)
    private String receiverAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false)
    private DeliveryMethod deliveryMethod;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 0)
    private BigDecimal totalAmount;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_source")
    private CancelSource cancelSource;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    protected Order() {
    }

    public Order(
            String orderCode,
            Long buyerId,
            Long sellerId,
            String receiverName,
            String receiverPhone,
            String receiverAddress,
            DeliveryMethod deliveryMethod,
            BigDecimal totalAmount,
            LocalDateTime expiresAt
    ) {
        this.orderCode = orderCode;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverAddress = receiverAddress;
        this.deliveryMethod = deliveryMethod;
        this.totalAmount = totalAmount;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public CancelSource getCancelSource() {
        return cancelSource;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void transitionTo(OrderStatus status, Long actorUserId, String reason, LocalDateTime changedAt) {
        this.status = status;
        if (status == OrderStatus.CONFIRMED) {
            confirmedAt = changedAt;
        } else if (status == OrderStatus.COMPLETED) {
            completedAt = changedAt;
        } else if (status == OrderStatus.REJECTED) {
            rejectReason = reason;
        } else if (status == OrderStatus.CANCELLED) {
            cancelReason = reason;
            cancelledAt = changedAt;
            cancelSource = actorUserId == null
                    ? CancelSource.SYSTEM
                    : actorUserId.equals(buyerId) ? CancelSource.BUYER : CancelSource.SELLER;
        }
    }

    public enum DeliveryMethod {
        MEET,
        SELLER_DELIVERS
    }

    public enum CancelSource {
        BUYER,
        SELLER,
        SYSTEM,
        ADMIN
    }
}
