package com.api.order.event;

import java.math.BigDecimal;

public record ProcessPaymentEvent(
    String orderId, BigDecimal amount, String cardNumber, String paymentMethod) {}
