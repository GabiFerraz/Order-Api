package com.api.order.presenter.response;

import lombok.Builder;

@Builder
public record OrderPresenterResponse(
    String id,
    String productSku,
    int productQuantity,
    String clientCpf,
    String status,
    PaymentDetailsPresenterResponse paymentDetails) {}
