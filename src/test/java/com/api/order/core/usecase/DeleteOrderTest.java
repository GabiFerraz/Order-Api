package com.api.order.core.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.PaymentDetails;
import com.api.order.core.domain.valueobject.OrderStatus;
import com.api.order.core.domain.valueobject.PaymentMethod;
import com.api.order.core.domain.valueobject.PaymentStatus;
import com.api.order.core.gateway.OrderGateway;
import com.api.order.core.usecase.exception.OrderNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DeleteOrderTest {

  private final OrderGateway orderGateway = mock(OrderGateway.class);
  private final DeleteOrder deleteOrder = new DeleteOrder(orderGateway);

  @Test
  void shouldDeleteOrderSuccessfullyWhenOrderExists() {
    final var id = "order-123";
    final var existingOrder =
        new Order(
            "1",
            "BOLA-123-ABC",
            10,
            "12345678901",
            OrderStatus.OPEN,
            new PaymentDetails(
                1, PaymentMethod.CREDIT_CARD, "1234567890123456", PaymentStatus.PENDING),
            BigDecimal.valueOf(1000.00),
            false);

    when(orderGateway.findById(id)).thenReturn(Optional.of(existingOrder));
    doNothing().when(orderGateway).delete(id);

    deleteOrder.execute(id);

    verify(orderGateway).findById(id);
    verify(orderGateway).delete(id);
  }

  @Test
  void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
    final var id = "order-999";

    when(orderGateway.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> deleteOrder.execute(id))
        .isInstanceOf(OrderNotFoundException.class)
        .hasMessage("Order with id=[" + id + "] not found.");

    verify(orderGateway).findById(id);
    verifyNoMoreInteractions(orderGateway);
  }
}
