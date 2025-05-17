package com.api.order.event;

public record ReleaseStockEvent(int orderId, String productSku, int quantity) {}
