package com.api.order.presenter.response;

import lombok.Builder;

@Builder
public record PaymentDetailsPresenterResponse(
    int id, String paymentMethod, String cardNumber, String status) {}
