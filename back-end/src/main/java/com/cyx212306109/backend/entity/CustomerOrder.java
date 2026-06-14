package com.cyx212306109.backend.entity;

import com.cyx212306109.backend.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class CustomerOrder extends BaseEntity {

    @Column(nullable = false, unique = true, length = 32)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id")
    private UserAccount rider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private OrderStatus refundPreviousStatus;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal goodsAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal payAmount;

    @Column(nullable = false, length = 32)
    private String contactName;

    @Column(nullable = false, length = 20)
    private String contactPhone;

    @Column(nullable = false, length = 255)
    private String addressSnapshot;

    @Column(length = 255)
    private String remark;

    @Column(nullable = false)
    private Boolean commented = false;

    @Column
    private LocalDateTime paidAt;

    @Column
    private LocalDateTime acceptedAt;

    @Column
    private LocalDateTime preparingAt;

    @Column
    private LocalDateTime readyAt;

    @Column
    private LocalDateTime deliveringAt;

    @Column
    private LocalDateTime deliveredAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column
    private LocalDateTime refundRequestedAt;

    @Column
    private LocalDateTime refundedAt;

    @Column
    private LocalDateTime refundRejectedAt;
}
