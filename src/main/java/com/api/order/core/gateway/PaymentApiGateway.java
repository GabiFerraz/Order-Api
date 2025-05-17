package com.api.order.core.gateway;

import java.math.BigDecimal;

public interface PaymentApiGateway {

  boolean processPayment(
      final String orderId,
      final BigDecimal amount,
      final String cardNumber,
      final String paymentMethod);
}
