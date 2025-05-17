package com.api.order.event;

public record PaymentProcessedEvent(String orderId, boolean success) {}
