package com.api.order.event;

public record ReserveStockEvent(String orderId, String productSku, int quantity) {}
