package com.api.order.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.PaymentDetails;
import com.api.order.core.domain.valueobject.OrderStatus;
import com.api.order.core.domain.valueobject.PaymentMethod;
import com.api.order.core.domain.valueobject.PaymentStatus;
import com.api.order.core.gateway.OrderGateway;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SearchOrderTest {

  private final OrderGateway orderGateway = mock(OrderGateway.class);
  private final SearchOrder searchOrder = new SearchOrder(orderGateway);

  @Test
  void shouldSearchOrderSuccessfully() {
    final var id = "1";
    final var expectedOrder =
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

    when(orderGateway.findById(id)).thenReturn(Optional.of(expectedOrder));

    final var response = searchOrder.execute(id);

    assertThat(response).isPresent();
    assertThat(response.get()).usingRecursiveComparison().isEqualTo(expectedOrder);

    verify(orderGateway).findById(id);
  }

  @Test
  void shouldReturnEmptyWhenOrderNotFound() {
    final var id = "1";

    when(orderGateway.findById(id)).thenReturn(Optional.empty());

    final var result = searchOrder.execute(id);

    assertThat(result).isEmpty();

    verify(orderGateway).findById(id);
  }
}
