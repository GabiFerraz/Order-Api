package com.api.order.core.domain.valueobject;

import static java.lang.String.format;

import com.api.order.core.domain.exception.DomainException;
import java.util.Arrays;

public enum PaymentStatus {
  PENDING,
  APPROVED,
  REJECTED;

  public static PaymentStatus fromName(final String status) {
    return Arrays.stream(values())
        .filter(it -> it.name().equalsIgnoreCase(status))
        .findAny()
        .orElseThrow(
            () -> new DomainException(format("The payment status=[%s] is invalid", status)));
  }
}
