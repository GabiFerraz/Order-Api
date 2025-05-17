package com.api.order.event;

import java.math.BigDecimal;

public record RefundPaymentEvent(int orderId, BigDecimal amount) {}
