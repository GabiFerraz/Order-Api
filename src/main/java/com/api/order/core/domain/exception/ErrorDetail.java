package com.api.order.core.domain.exception;

public record ErrorDetail(String field, String errorMessage, Object rejectedValue) {}
