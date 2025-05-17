package com.api.order.core.dto;

import lombok.Builder;

@Builder
public record OrderDto(
    String productSku,
    Integer productQuantity,
    String clientCpf,
    String status,
    PaymentDetailsDto paymentDetails) {}
