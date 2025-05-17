package com.api.order.core.usecase.exception;

import static java.lang.String.format;

public class OrderNotFoundException extends BusinessException {

  private static final String ERROR_CODE = "NOT_FOUND";
  private static final String MESSAGE = "Stock for sku=[%s] not found.";

  public OrderNotFoundException(final String id) {
    super(format(MESSAGE, id), ERROR_CODE);
  }
}
