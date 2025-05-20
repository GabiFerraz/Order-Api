package com.api.order.infra.gateway.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.api.order.config.RabbitMQConfig;
import com.api.order.event.ProcessPaymentEvent;
import com.api.order.event.RefundPaymentEvent;
import com.api.order.event.ReleaseStockEvent;
import com.api.order.event.ReserveStockEvent;
import com.api.order.infra.gateway.exception.GatewayException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

class RabbitMQGatewayTest {

  private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
  private final RabbitMQGateway rabbitMQGateway = new RabbitMQGateway(rabbitTemplate);

  @Test
  void shouldPublishReserveStockEventSuccessfully() {
    final var event = new ReserveStockEvent("order-123", "BOLA-123-ABC", 10);
    final var expectedRoutingKey = "reserve-stock";

    rabbitMQGateway.publish(event);

    final ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<ReserveStockEvent> eventCaptor =
        ArgumentCaptor.forClass(ReserveStockEvent.class);

    verify(rabbitTemplate)
        .convertAndSend(
            exchangeCaptor.capture(), routingKeyCaptor.capture(), eventCaptor.capture());

    assertThat(exchangeCaptor.getValue()).isEqualTo(RabbitMQConfig.EXCHANGE_NAME);
    assertThat(routingKeyCaptor.getValue()).isEqualTo(expectedRoutingKey);
    assertThat(eventCaptor.getValue()).isEqualTo(event);
    assertThat(eventCaptor.getValue().orderId()).isEqualTo("order-123");
    assertThat(eventCaptor.getValue().productSku()).isEqualTo("BOLA-123-ABC");
    assertThat(eventCaptor.getValue().quantity()).isEqualTo(10);
  }

  @Test
  void shouldPublishProcessPaymentEventSuccessfully() {
    final var event =
        new ProcessPaymentEvent(
            "order-123", BigDecimal.valueOf(100.00), "1234567890123456", "CREDIT_CARD");
    final var expectedRoutingKey = "process-payment";

    rabbitMQGateway.publish(event);

    final ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<ProcessPaymentEvent> eventCaptor =
        ArgumentCaptor.forClass(ProcessPaymentEvent.class);

    verify(rabbitTemplate)
        .convertAndSend(
            exchangeCaptor.capture(), routingKeyCaptor.capture(), eventCaptor.capture());

    assertThat(exchangeCaptor.getValue()).isEqualTo(RabbitMQConfig.EXCHANGE_NAME);
    assertThat(routingKeyCaptor.getValue()).isEqualTo(expectedRoutingKey);
    assertThat(eventCaptor.getValue()).isEqualTo(event);
    assertThat(eventCaptor.getValue().orderId()).isEqualTo("order-123");
    assertThat(eventCaptor.getValue().cardNumber()).isEqualTo("1234567890123456");
    assertThat(eventCaptor.getValue().amount()).isEqualTo(BigDecimal.valueOf(100.00));
  }

  @Test
  void shouldPublishReleaseStockEventSuccessfully() {
    final var event = new ReleaseStockEvent("order-123", "BOLA-123-ABC", 10);
    final var expectedRoutingKey = "release-stock";

    rabbitMQGateway.publish(event);

    final ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<ReleaseStockEvent> eventCaptor =
        ArgumentCaptor.forClass(ReleaseStockEvent.class);

    verify(rabbitTemplate)
        .convertAndSend(
            exchangeCaptor.capture(), routingKeyCaptor.capture(), eventCaptor.capture());

    assertThat(exchangeCaptor.getValue()).isEqualTo(RabbitMQConfig.EXCHANGE_NAME);
    assertThat(routingKeyCaptor.getValue()).isEqualTo(expectedRoutingKey);
    assertThat(eventCaptor.getValue()).isEqualTo(event);
    assertThat(eventCaptor.getValue().orderId()).isEqualTo("order-123");
    assertThat(eventCaptor.getValue().productSku()).isEqualTo("BOLA-123-ABC");
    assertThat(eventCaptor.getValue().quantity()).isEqualTo(10);
  }

  @Test
  void shouldPublishRefundPaymentEventSuccessfully() {
    final var event = new RefundPaymentEvent("order-123", BigDecimal.valueOf(100.00));
    final var expectedRoutingKey = "refund-payment";

    rabbitMQGateway.publish(event);

    final ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<RefundPaymentEvent> eventCaptor =
        ArgumentCaptor.forClass(RefundPaymentEvent.class);

    verify(rabbitTemplate)
        .convertAndSend(
            exchangeCaptor.capture(), routingKeyCaptor.capture(), eventCaptor.capture());

    assertThat(exchangeCaptor.getValue()).isEqualTo(RabbitMQConfig.EXCHANGE_NAME);
    assertThat(routingKeyCaptor.getValue()).isEqualTo(expectedRoutingKey);
    assertThat(eventCaptor.getValue()).isEqualTo(event);
    assertThat(eventCaptor.getValue().orderId()).isEqualTo("order-123");
    assertThat(eventCaptor.getValue().amount()).isEqualTo(BigDecimal.valueOf(100.00));
  }

  @Test
  void shouldThrowGatewayExceptionWhenPublishFails() {
    final var event = new ReserveStockEvent("order-123", "BOLA-123-ABC", 10);
    final var exception = new RuntimeException("AMQP error");

    doThrow(exception)
        .when(rabbitTemplate)
        .convertAndSend(any(String.class), any(String.class), any(Object.class));

    assertThatThrownBy(() -> rabbitMQGateway.publish(event))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Failed to publish event: " + event);

    verify(rabbitTemplate)
        .convertAndSend(eq(RabbitMQConfig.EXCHANGE_NAME), eq("reserve-stock"), eq(event));
  }
}
