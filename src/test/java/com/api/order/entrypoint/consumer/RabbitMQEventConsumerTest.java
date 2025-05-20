package com.api.order.entrypoint.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.api.order.core.dto.OrderDto;
import com.api.order.core.dto.PaymentDetailsDto;
import com.api.order.core.usecase.CreateOrder;
import com.api.order.core.usecase.HandleOrderEvents;
import com.api.order.event.OrderReceivedEvent;
import com.api.order.event.PaymentProcessedEvent;
import com.api.order.event.StockReservedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class RabbitMQEventConsumerTest {

  private final HandleOrderEvents handleOrderEvents = mock(HandleOrderEvents.class);
  private final CreateOrder createOrder = mock(CreateOrder.class);
  private final RabbitMQEventConsumer eventConsumer =
      new RabbitMQEventConsumer(handleOrderEvents, createOrder);

  @Test
  void shouldHandleStockReservedEventSuccessfully() {
    final var event = new StockReservedEvent("order-123", true);

    eventConsumer.consumeStockReservedEvent(event);

    final ArgumentCaptor<StockReservedEvent> eventCaptor =
        ArgumentCaptor.forClass(StockReservedEvent.class);
    verify(handleOrderEvents).handleStockReservedEvent(eventCaptor.capture());

    assertThat(eventCaptor.getValue()).isEqualTo(event);
    assertThat(eventCaptor.getValue().orderId()).isEqualTo("order-123");
    assertThat(eventCaptor.getValue().success()).isTrue();
  }

  @Test
  void shouldHandlePaymentProcessedEventSuccessfully() {
    final var event = new PaymentProcessedEvent("order-123", true);

    eventConsumer.consumePaymentProcessedEvent(event);

    final ArgumentCaptor<PaymentProcessedEvent> eventCaptor =
        ArgumentCaptor.forClass(PaymentProcessedEvent.class);
    verify(handleOrderEvents).handlePaymentProcessedEvent(eventCaptor.capture());

    assertThat(eventCaptor.getValue()).isEqualTo(event);
    assertThat(eventCaptor.getValue().orderId()).isEqualTo("order-123");
    assertThat(eventCaptor.getValue().success()).isTrue();
  }

  @Test
  void shouldProcessOrderReceivedEventSuccessfully() {
    final var event =
        new OrderReceivedEvent("sku-123", 10, "12345678901", "CREDIT_CARD", "1234567890123456");
    final var expectedOrderDto =
        OrderDto.builder()
            .productSku("sku-123")
            .productQuantity(10)
            .clientCpf("12345678901")
            .paymentDetails(new PaymentDetailsDto("CREDIT_CARD", "1234567890123456"))
            .build();

    eventConsumer.consumeOrderReceivedEvent(event);

    final ArgumentCaptor<OrderDto> orderDtoCaptor = ArgumentCaptor.forClass(OrderDto.class);
    verify(createOrder).execute(orderDtoCaptor.capture());

    final var capturedOrderDto = orderDtoCaptor.getValue();
    assertThat(capturedOrderDto).usingRecursiveComparison().isEqualTo(expectedOrderDto);
    assertThat(capturedOrderDto.productSku()).isEqualTo(event.productSku());
    assertThat(capturedOrderDto.productQuantity()).isEqualTo(event.productQuantity());
    assertThat(capturedOrderDto.clientCpf()).isEqualTo(event.clientCpf());
    assertThat(capturedOrderDto.paymentDetails().paymentMethod()).isEqualTo(event.paymentMethod());
    assertThat(capturedOrderDto.paymentDetails().cardNumber()).isEqualTo(event.cardNumber());
  }

  @Test
  void shouldThrowRuntimeExceptionWhenOrderReceivedEventProcessingFails() {
    final var event =
        new OrderReceivedEvent("sku-123", 10, "12345678901", "CREDIT_CARD", "1234567890123456");
    final var exception = new RuntimeException("Order creation failed");

    doThrow(exception).when(createOrder).execute(any(OrderDto.class));

    assertThatThrownBy(() -> eventConsumer.consumeOrderReceivedEvent(event))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Failed to process order")
        .hasCause(exception);

    verify(createOrder).execute(any(OrderDto.class));
  }
}
