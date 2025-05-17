package com.api.order.core.dto;

public record OrderDto(
    String productSku,
    Integer productQuantity,
    String clientCpf,
    String status,
    PaymentDetailsDto paymentDetails) {}
