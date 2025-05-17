package com.api.order.infra.gateway.queue;

import com.api.order.config.RabbitMQConfig;
import com.api.order.core.gateway.EventPublisher;
import com.api.order.event.ProcessPaymentEvent;
import com.api.order.event.RefundPaymentEvent;
import com.api.order.event.ReleaseStockEvent;
import com.api.order.event.ReserveStockEvent;
import com.api.order.infra.gateway.exception.GatewayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQGateway implements EventPublisher {

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publish(final Object event) {
    try {
      final String routingKey =
          switch (event) {
            case ReserveStockEvent e -> "reserve-stock";
            case ProcessPaymentEvent e -> "process-payment";
            case ReleaseStockEvent e -> "release-stock";
            case RefundPaymentEvent e -> "refund-payment";
            default -> throw new IllegalArgumentException("Unknown event: " + event.getClass());
          };

      this.rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
    } catch (Exception e) {
      log.error("Failed to publish event: {}", event, e);
      throw new GatewayException("Failed to publish event: " + event);
    }
  }
}
