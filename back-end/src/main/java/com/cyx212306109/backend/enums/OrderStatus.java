package com.cyx212306109.backend.enums;

public enum OrderStatus {
    PENDING_PAYMENT("待支付"),
    PAID("已支付"),
    ACCEPTED("已接单"),
    PREPARING("制作中"),
    READY_FOR_DELIVERY("待配送"),
    DELIVERING("配送中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
