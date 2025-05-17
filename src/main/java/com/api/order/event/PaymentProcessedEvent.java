package com.api.order.event;

public record PaymentProcessedEvent(int orderId, boolean success) {}
