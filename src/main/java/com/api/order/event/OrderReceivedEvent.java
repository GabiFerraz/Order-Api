package com.api.order.event;

public record OrderReceivedEvent(
    String productSku,
    int productQuantity,
    String clientCpf,
    String paymentMethod,
    String cardNumber) {}
