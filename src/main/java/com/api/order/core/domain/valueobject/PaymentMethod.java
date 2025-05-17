package com.api.order.core.domain.valueobject;

import static java.lang.String.format;

import com.api.order.core.domain.exception.DomainException;
import java.util.Arrays;

public enum PaymentMethod {
  CREDIT_CARD,
  DEBIT_CARD;

  public static PaymentMethod fromName(final String paymentMethod) {
    return Arrays.stream(values())
        .filter(it -> it.name().equalsIgnoreCase(paymentMethod))
        .findAny()
        .orElseThrow(
            () -> new DomainException(format("The payment method=[%s] is invalid", paymentMethod)));
  }
}
