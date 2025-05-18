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
    log.info(
        "Processing StockReservedEvent for orderId: {}, success: {}",
        event.orderId(),
        event.success());

    final var order =
        this.orderGateway
            .findById(event.orderId())
            .orElseThrow(() -> new OrderNotFoundException(event.orderId()));
    log.info(
        "Order before update: id={}, stockReserved={}, paymentStatus={}, orderStatus={}",
        order.getId(),
        order.isStockReserved(),
        order.getPaymentDetails().getStatus(),
        order.getStatus());

    if (!event.success()) {
      log.info("Stock reservation failed, checking payment status");
      if (order.getPaymentDetails().getStatus() == PaymentStatus.APPROVED) {
        log.info(
            "Issuing refund for orderId: {}, status: {}",
            order.getId(),
            order.getPaymentDetails().getStatus());

        this.eventPublisher.publish(new RefundPaymentEvent(order.getId(), order.getTotalAmount()));
      }

      if (order.getStatus() != OrderStatus.CLOSED_WITHOUT_STOCK) {
        final var orderUpdated = order.changeOrderStatus(OrderStatus.CLOSED_WITHOUT_STOCK);
        log.info("Updating order to CLOSED_WITHOUT_STOCK: {}", orderUpdated.getStatus());

        this.orderGateway.update(orderUpdated);
      }
      return;
    }

    final var refreshedOrder =
        orderGateway
            .findById(event.orderId())
            .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

    if (refreshedOrder.isStockReserved()) {
      log.info("Stock already reserved for orderId: {}, skipping update", refreshedOrder.getId());
      updateOrderStatus(refreshedOrder);
      return;
    }

    log.info("Stock reservation successful, setting stockReserved=true");
    final var orderUpdated = order.setStockReserved(true);
    log.info("Updating order to stockReserved=true: {}", orderUpdated.isStockReserved());

    this.orderGateway.update(orderUpdated);

    log.info("Order persisted with stockReserved=true and checking if order can be closed");

    this.updateOrderStatus(orderUpdated);
  }

  @Transactional
  public void handlePaymentProcessedEvent(final PaymentProcessedEvent event) {
    log.info(
        "Processing PaymentProcessedEvent for orderId: {}, success: {}",
        event.orderId(),
        event.success());

    final var order =
        this.orderGateway
            .findById(event.orderId())
            .orElseThrow(() -> new OrderNotFoundException(event.orderId()));
    log.info(
        "Order before update: id={}, stockReserved={}, paymentStatus={}, orderStatus={}",
        order.getId(),
        order.isStockReserved(),
        order.getPaymentDetails().getStatus(),
        order.getStatus());

    if (!event.success()) {
      log.info("Payment failed, checking stock reservation");
      if (order.isStockReserved()) {
        log.info("Releasing stock for orderId: {}", order.getId());
        this.eventPublisher.publish(
            new ReleaseStockEvent(
                order.getId(), order.getProductSku(), order.getProductQuantity()));
      }

      final var orderUpdated =
          order
              .changeOrderStatus(OrderStatus.CLOSED_WITHOUT_CREDIT)
              .updatePaymentStatus(PaymentStatus.REJECTED);
      log.info(
          "Updating order to CLOSED_WITHOUT_CREDIT: {} and payment REJECTED: {}",
          orderUpdated.getStatus(),
          orderUpdated.getPaymentDetails().getStatus());

      this.orderGateway.update(orderUpdated);
      return;
    }

    if (order.getStatus() == OrderStatus.CLOSED_WITHOUT_STOCK) {
      log.info("Order is CLOSED_WITHOUT_STOCK, issuing refund for orderId: {}", order.getId());
      this.eventPublisher.publish(new RefundPaymentEvent(order.getId(), order.getTotalAmount()));

      final var orderUpdated = order.updatePaymentStatus(PaymentStatus.REFUNDED);
      log.info("Updating order to paymentStatus=REFUNDED: {}", orderUpdated.getStatus());

      this.orderGateway.update(orderUpdated);
      return;
    }

    log.info("Payment successful, setting paymentStatus=APPROVED");
    final var orderUpdated = order.updatePaymentStatus(PaymentStatus.APPROVED);
    log.info("Updating order to paymentStatus=APPROVED: {}", orderUpdated.getStatus());

    orderGateway.update(orderUpdated);

    log.info("Order persisted with paymentStatus=APPROVED and checking if order can be closed");

    this.updateOrderStatus(orderUpdated);
  }

  private void updateOrderStatus(final Order order) {
    log.info(
        "Checking order status: id={}, stockReserved={}, paymentStatus={}, orderStatus={}",
        order.getId(),
        order.isStockReserved(),
        order.getPaymentDetails().getStatus(),
        order.getStatus());

    if (order.isStockReserved()
        && order.getPaymentDetails().getStatus() == PaymentStatus.APPROVED) {
      final var orderUpdated = order.changeOrderStatus(OrderStatus.CLOSED_WITH_SUCCESS);

      log.info(
          "Closing order with success: id={}, status={}",
          orderUpdated.getId(),
          orderUpdated.getStatus());

      this.orderGateway.update(orderUpdated);
    } else {
      log.warn(
          "Order not closed: id={}, stockReserved={}, paymentStatus={}, orderStatus={}",
          order.getId(),
          order.isStockReserved(),
          order.getPaymentDetails().getStatus(),
          order.getStatus());
    }
  }
}
