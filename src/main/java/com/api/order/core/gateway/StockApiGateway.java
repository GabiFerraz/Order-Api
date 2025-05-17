package com.api.order.core.gateway;

public interface StockApiGateway {

  boolean reserveStock(final String productSku, final int quantity);
}
