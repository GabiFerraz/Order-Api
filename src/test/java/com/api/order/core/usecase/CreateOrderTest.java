package com.api.order.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.PaymentDetails;
import com.api.order.core.domain.valueobject.OrderStatus;
import com.api.order.core.domain.valueobject.PaymentMethod;
import com.api.order.core.domain.valueobject.PaymentStatus;
import com.api.order.core.dto.OrderDto;
import com.api.order.core.dto.PaymentDetailsDto;
import com.api.order.core.gateway.EventPublisher;
import com.api.order.core.gateway.OrderGateway;
import com.api.order.core.gateway.ProductApiGateway;
import com.api.order.core.usecase.exception.ProductNotFoundException;
import com.api.order.event.ProcessPaymentEvent;
import com.api.order.event.ReserveStockEvent;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CreateOrderTest {

  private final OrderGateway orderGateway = mock(OrderGateway.class);
  private final ProductApiGateway productApiGateway = mock(ProductApiGateway.class);
  private final EventPublisher eventPublisher = mock(EventPublisher.class);
  private final CreateOrder createOrder =
      new CreateOrder(orderGateway, productApiGateway, eventPublisher);

  @Test
  void shouldCreateOrderSuccessfully() {
    final var request =
        OrderDto.builder()
            .productSku("BOLA-123-ABC")
            .productQuantity(10)
            .clientCpf("12345678901")
            .paymentDetails(new PaymentDetailsDto("CREDIT_CARD", "1234567890123456"))
            .build();
    final var unitPrice = BigDecimal.valueOf(100.00);
    final var savedOrder =
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

    when(productApiGateway.getProductPrice(request.productSku())).thenReturn(unitPrice);
    doReturn(savedOrder).when(orderGateway).save(any(Order.class));
    doNothing().when(eventPublisher).publish(any(ReserveStockEvent.class));
    doNothing().when(eventPublisher).publish(any(ProcessPaymentEvent.class));

    final var response = createOrder.execute(request);

    assertThat(response).isEqualTo(savedOrder);
    assertThat(response.getProductSku()).isEqualTo(request.productSku());
    assertThat(response.getProductQuantity()).isEqualTo(request.productQuantity());
    assertThat(response.getClientCpf()).isEqualTo(request.clientCpf());
    assertThat(response.getPaymentDetails().getPaymentMethod())
        .isEqualTo(PaymentMethod.CREDIT_CARD);
    assertThat(response.getPaymentDetails().getCardNumber())
        .isEqualTo(request.paymentDetails().cardNumber());
    assertThat(response.getTotalAmount()).isEqualTo(BigDecimal.valueOf(1000.00));

    verify(productApiGateway).getProductPrice(request.productSku());
    verify(orderGateway).save(any());
    verify(eventPublisher, times(2)).publish(any());
  }

  @Test
  void shouldThrowProductNotFoundExceptionWhenProductPriceIsNull() {
    final var request =
        OrderDto.builder()
            .productSku("sku-123")
            .productQuantity(10)
            .clientCpf("12345678901")
            .paymentDetails(new PaymentDetailsDto("CREDIT_CARD", "1234567890123456"))
            .build();

    when(productApiGateway.getProductPrice(request.productSku())).thenReturn(null);

    assertThatThrownBy(() -> createOrder.execute(request))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage("Product with sku=[" + request.productSku() + "] not found.");

    verify(productApiGateway).getProductPrice(request.productSku());
  }
}
