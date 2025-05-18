package com.api.order.core.usecase;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.PaymentDetails;
import com.api.order.core.domain.valueobject.PaymentMethod;
import com.api.order.core.dto.OrderDto;
import com.api.order.core.dto.PaymentDetailsDto;
import com.api.order.core.gateway.EventPublisher;
import com.api.order.core.gateway.OrderGateway;
import com.api.order.core.gateway.ProductApiGateway;
import com.api.order.core.usecase.exception.ProductNotFoundException;
import com.api.order.event.ProcessPaymentEvent;
import com.api.order.event.ReserveStockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOrder {

  private final OrderGateway orderGateway;
  private final ProductApiGateway productApiGateway;
  private final EventPublisher eventPublisher;

  public Order execute(final OrderDto request) {
    final var unitPrice = productApiGateway.getProductPrice(request.productSku());
    if (unitPrice == null) {
      throw new ProductNotFoundException(request.productSku());
    }

    final var paymentDetails = toPaymentDetails(request.paymentDetails());

    final var buildDomain =
        Order.createOrder(
            request.productSku(),
            request.productQuantity(),
            request.clientCpf(),
            paymentDetails,
            unitPrice);

    final var savedOrder = this.orderGateway.save(buildDomain);

    eventPublisher.publish(
        new ReserveStockEvent(
            savedOrder.getId(), savedOrder.getProductSku(), savedOrder.getProductQuantity()));

    eventPublisher.publish(
        new ProcessPaymentEvent(
            savedOrder.getId(),
            savedOrder.getTotalAmount(),
            paymentDetails.getCardNumber(),
            paymentDetails.getPaymentMethod().name()));

    return savedOrder;
  }

  private PaymentDetails toPaymentDetails(final PaymentDetailsDto paymentDetailsDto) {
    return PaymentDetails.createPaymentDetails(
        PaymentMethod.fromName(paymentDetailsDto.paymentMethod()), paymentDetailsDto.cardNumber());
  }
}
