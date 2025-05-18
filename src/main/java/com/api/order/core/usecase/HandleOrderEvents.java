package com.api.order.core.usecase;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.valueobject.OrderStatus;
import com.api.order.core.domain.valueobject.PaymentStatus;
import com.api.order.core.gateway.EventPublisher;
import com.api.order.core.gateway.OrderGateway;
import com.api.order.core.usecase.exception.OrderNotFoundException;
import com.api.order.event.PaymentProcessedEvent;
import com.api.order.event.RefundPaymentEvent;
import com.api.order.event.ReleaseStockEvent;
import com.api.order.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class HandleOrderEvents {

  private final OrderGateway orderGateway;
  private final EventPublisher eventPublisher;

  @Transactional
  public void handleStockReservedEvent(final StockReservedEvent event) {
    log.info("Received StockReservedEvent for orderId: {}", event.orderId());

    final var order =
        orderGateway
            .findById(event.orderId())
            .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

    if (!event.success()) {
      if (order.getPaymentDetails().getStatus() == PaymentStatus.APPROVED) {
        eventPublisher.publish(new RefundPaymentEvent(order.getId(), order.getTotalAmount()));
      }

      final var orderUpdated = order.changeOrderStatus(OrderStatus.CLOSED_WITHOUT_STOCK);

      orderGateway.update(orderUpdated);
      return;
    }

    final var orderUpdated = order.setStockReserved(true);

    orderGateway.update(orderUpdated);

    this.updateOrderStatus(orderUpdated);
  }

  @Transactional
  public void handlePaymentProcessedEvent(final PaymentProcessedEvent event) {
    log.info("Received PaymentProcessedEvent for orderId: {}", event.orderId());

    final var order =
        orderGateway
            .findById(event.orderId())
            .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

    if (!event.success()) {
      if (order.isStockReserved()) {
        eventPublisher.publish(
            new ReleaseStockEvent(
                order.getId(), order.getProductSku(), order.getProductQuantity()));
      }

      final var orderUpdated =
          order
              .changeOrderStatus(OrderStatus.CLOSED_WITHOUT_CREDIT)
              .updatePaymentStatus(PaymentStatus.REJECTED);

      orderGateway.update(orderUpdated);
      return;
    }

    final var orderUpdated = order.updatePaymentStatus(PaymentStatus.APPROVED);

    orderGateway.update(orderUpdated);

    this.updateOrderStatus(orderUpdated);
  }

  private void updateOrderStatus(final Order order) {
    if (order.isStockReserved()
        && order.getPaymentDetails().getStatus() == PaymentStatus.APPROVED) {
      final var orderUpdated = order.changeOrderStatus(OrderStatus.CLOSED_WITH_SUCCESS);

      log.info("Order closed with success: {}", orderUpdated);
      orderGateway.update(orderUpdated);
    }
  }
}
