package com.api.order.core.domain.valueobject;

import static java.lang.String.format;

import com.api.order.core.domain.exception.DomainException;
import java.util.Arrays;

public enum OrderStatus {
  OPEN,
  CLOSED_WITH_SUCCESS,
  CLOSED_WITHOUT_STOCK,
  CLOSED_WITHOUT_CREDIT;

  public static OrderStatus fromName(final String status) {
    return Arrays.stream(values())
        .filter(it -> it.name().equalsIgnoreCase(status))
        .findAny()
        .orElseThrow(() -> new DomainException(format("The order status=[%s] is invalid", status)));
  }
}
