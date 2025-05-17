package com.api.order.core.usecase;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.valueobject.OrderStatus;
import com.api.order.core.domain.valueobject.PaymentStatus;
import com.api.order.core.gateway.OrderGateway;
import com.api.order.core.usecase.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateOrder {

  private final OrderGateway orderGateway;

  public Order execute(final int id, final String orderStatus, final String paymentStatus) {
    final var existingOrder =
        this.orderGateway.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

    existingOrder.changeOrderStatus(OrderStatus.fromName(orderStatus));
    existingOrder.getPaymentDetails().changePaymentStatus(PaymentStatus.fromName(paymentStatus));

    return this.orderGateway.save(existingOrder);
  }
}
