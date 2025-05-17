package com.api.order.entrypoint.controller.consumer;

import com.api.order.config.RabbitMQConfig;
import com.api.order.core.usecase.HandleOrderEvents;
import com.api.order.event.PaymentProcessedEvent;
import com.api.order.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventConsumer {

  private final HandleOrderEvents handleOrderEvents;

  @RabbitListener(queues = RabbitMQConfig.STOCK_RESERVED_QUEUE)
  public void consumeStockReservedEvent(final StockReservedEvent event) {
    handleOrderEvents.handleStockReservedEvent(event);
  }

  @RabbitListener(queues = RabbitMQConfig.PAYMENT_PROCESSED_QUEUE)
  public void consumePaymentProcessedEvent(final PaymentProcessedEvent event) {
    handleOrderEvents.handlePaymentProcessedEvent(event);
  }
}
