package com.api.order.core.domain.exception;

import java.util.List;
import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

  private static final String DEFAULT_CODE = "domain_exception";
  private final String code;
  private final List<String> messages;

  public DomainException(final List<String> messages) {
    super(String.join(", ", messages));
    this.code = DEFAULT_CODE;
    this.messages = messages;
  }

  public DomainException(final String message) {
    super(message);
    this.code = DEFAULT_CODE;
    this.messages = List.of(message);
  }
}
