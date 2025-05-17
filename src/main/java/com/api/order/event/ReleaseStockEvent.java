package com.api.order.event;

public record ReleaseStockEvent(String orderId, String productSku, int quantity) {}
