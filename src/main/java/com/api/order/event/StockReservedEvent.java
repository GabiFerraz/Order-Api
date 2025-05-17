package com.api.order.event;

public record StockReservedEvent(String orderId, boolean success) {}
