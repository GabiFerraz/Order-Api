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
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandleOrderEvents {

  private final OrderGateway orderGateway;
  private final EventPublisher eventPublisher;

  private static class OrderState {
    boolean stockReserved;
    boolean paymentApproved;

    OrderState() {
      this.stockReserved = false;
      this.paymentApproved = false;
    }
  }

  public void handleStockReservedEvent(final StockReservedEvent event) {
    var order =
        orderGateway
            .findById(event.orderId())
            .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

    OrderState state = getOrderState(order);

    if (!event.success()) {
      if (state.paymentApproved) {
        eventPublisher.publish(new RefundPaymentEvent(order.getId(), order.getTotalAmount()));
      }

      order = order.changeOrderStatus(OrderStatus.CLOSED_WITHOUT_STOCK);
      orderGateway.save(order);

      return;
    }

    state.stockReserved = true;

    this.updateOrderStatus(order, state);
  }

  public void handlePaymentProcessedEvent(PaymentProcessedEvent event) {
    var order =
        orderGateway
            .findById(event.orderId())
            .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

    OrderState state = getOrderState(order);

    if (!event.success()) {
      if (state.stockReserved) {
        eventPublisher.publish(
            new ReleaseStockEvent(
                order.getId(), order.getProductSku(), order.getProductQuantity()));
      }

      order =
          order
              .changeOrderStatus(OrderStatus.CLOSED_WITHOUT_CREDIT)
              .updatePaymentStatus(PaymentStatus.REJECTED);
      orderGateway.save(order);

      return;
    }

    state.paymentApproved = true;
    order = order.updatePaymentStatus(PaymentStatus.APPROVED);

    this.updateOrderStatus(order, state);
  }

  private OrderState getOrderState(final Order order) {
    OrderState state = new OrderState();

    state.stockReserved =
        order.getStatus() == OrderStatus.CLOSED_WITH_SUCCESS
            || order.getStatus() == OrderStatus.CLOSED_WITHOUT_CREDIT;
    state.paymentApproved =
        order.getPaymentDetails().getStatus() == PaymentStatus.APPROVED
            || order.getStatus() == OrderStatus.CLOSED_WITHOUT_STOCK;

    return state;
  }

  private void updateOrderStatus(Order order, final OrderState state) {
    if (state.stockReserved && state.paymentApproved) {
      order = order.changeOrderStatus(OrderStatus.CLOSED_WITH_SUCCESS);

      orderGateway.save(order);
    }
  }
}
