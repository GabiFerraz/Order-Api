package com.api.order.core.gateway;

import java.math.BigDecimal;

public interface ProductApiGateway {

  BigDecimal getProductPrice(final String productSku);
}
