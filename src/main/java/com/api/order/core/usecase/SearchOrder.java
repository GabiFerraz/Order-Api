package com.api.order.core.usecase;

import com.api.order.core.domain.Order;
import com.api.order.core.gateway.OrderGateway;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchOrder {

  private final OrderGateway orderGateway;

  public Optional<Order> execute(final String id) {
    return this.orderGateway.findById(id);
  }
}
