package com.api.order.event;

public record StockReservedEvent(int orderId, boolean success) {}
