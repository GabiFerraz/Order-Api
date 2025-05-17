package com.api.order.event;

import java.math.BigDecimal;

public record RefundPaymentEvent(String orderId, BigDecimal amount) {}
