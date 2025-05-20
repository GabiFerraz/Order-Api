package com.api.order.entrypoint.consumer;

import com.api.order.config.RabbitMQConfig;
import com.api.order.core.dto.OrderDto;
import com.api.order.core.dto.PaymentDetailsDto;
import com.api.order.core.usecase.CreateOrder;
import com.api.order.core.usecase.HandleOrderEvents;
import com.api.order.event.OrderReceivedEvent;
import com.api.order.event.PaymentProcessedEvent;
import com.api.order.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQEventConsumer {

  private final HandleOrderEvents handleOrderEvents;
  private final CreateOrder createOrder;

  @RabbitListener(queues = RabbitMQConfig.STOCK_RESERVED_QUEUE)
  public void consumeStockReservedEvent(final StockReservedEvent event) {
    log.info("Received StockReservedEvent for orderId: {}", event.orderId());
    handleOrderEvents.handleStockReservedEvent(event);
  }

  @RabbitListener(queues = RabbitMQConfig.PAYMENT_PROCESSED_QUEUE)
  public void consumePaymentProcessedEvent(final PaymentProcessedEvent event) {
    log.info("Received PaymentProcessedEvent for orderId: {}", event.orderId());
    handleOrderEvents.handlePaymentProcessedEvent(event);
  }

  @RabbitListener(queues = RabbitMQConfig.ORDER_RECEIVED_QUEUE)
  public void consumeOrderReceivedEvent(final OrderReceivedEvent event) {
    log.info("Processing OrderReceivedEvent for productSku: {}", event.productSku());
    try {
      final var orderDto =
          OrderDto.builder()
              .productSku(event.productSku())
              .productQuantity(event.productQuantity())
              .clientCpf(event.clientCpf())
              .paymentDetails(new PaymentDetailsDto(event.paymentMethod(), event.cardNumber()))
              .build();

      this.createOrder.execute(orderDto);
      log.info("Order processed successfully for event with productSku: {}", event.productSku());
    } catch (Exception e) {
      log.error(
          "Failed to process OrderReceivedEvent for event with productSku: {}",
          event.productSku(),
          e);
      throw new RuntimeException("Failed to process order", e);
    }
  }
}
