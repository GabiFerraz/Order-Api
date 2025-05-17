package com.api.order.core.usecase;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.PaymentDetails;
import com.api.order.core.domain.valueobject.PaymentMethod;
import com.api.order.core.dto.OrderDto;
import com.api.order.core.dto.PaymentDetailsDto;
import com.api.order.core.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOrder {

  private final OrderGateway orderGateway;

  public Order execute(final OrderDto request) {
    final var buildDomain =
        Order.createOrder(
            request.productSku(),
            request.productQuantity(),
            request.clientCpf(),
            this.toPaymentDetails(request.paymentDetails()));

    return this.orderGateway.save(buildDomain);
  }

  private PaymentDetails toPaymentDetails(final PaymentDetailsDto paymentDetailsDto) {
    return PaymentDetails.createPaymentDetails(
        PaymentMethod.fromName(paymentDetailsDto.paymentMethod()), paymentDetailsDto.cardNumber());
  }
}
