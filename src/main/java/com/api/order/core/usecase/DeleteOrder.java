package com.api.order.core.usecase;

import com.api.order.core.gateway.OrderGateway;
import com.api.order.core.usecase.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteOrder {

  private final OrderGateway orderGateway;

  public void execute(final String id) {
    this.orderGateway.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    this.orderGateway.delete(id);
  }
}
