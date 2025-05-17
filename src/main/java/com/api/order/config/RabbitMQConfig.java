package com.api.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String EXCHANGE_NAME = "order.events";
  public static final String RESERVE_STOCK_QUEUE = "reserve-stock";
  public static final String PROCESS_PAYMENT_QUEUE = "process-payment";
  public static final String RELEASE_STOCK_QUEUE = "release-stock";
  public static final String REFUND_PAYMENT_QUEUE = "refund-payment";
  public static final String STOCK_RESERVED_QUEUE = "stock-reserved";
  public static final String PAYMENT_PROCESSED_QUEUE = "payment-processed";
  public static final String ORDER_RECEIVED_QUEUE = "order-received";

  @Bean
  public TopicExchange orderExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue reserveStockQueue() {
    return new Queue(RESERVE_STOCK_QUEUE, true);
  }

  @Bean
  public Queue processPaymentQueue() {
    return new Queue(PROCESS_PAYMENT_QUEUE, true);
  }

  @Bean
  public Queue releaseStockQueue() {
    return new Queue(RELEASE_STOCK_QUEUE, true);
  }

  @Bean
  public Queue refundPaymentQueue() {
    return new Queue(REFUND_PAYMENT_QUEUE, true);
  }

  @Bean
  public Queue stockReservedQueue() {
    return new Queue(STOCK_RESERVED_QUEUE, true);
  }

  @Bean
  public Queue paymentProcessedQueue() {
    return new Queue(PAYMENT_PROCESSED_QUEUE, true);
  }

  @Bean
  public Queue orderReceivedQueue() {
    return new Queue(ORDER_RECEIVED_QUEUE, true);
  }

  @Bean
  public Binding reserveStockBinding(Queue reserveStockQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(reserveStockQueue).to(orderExchange).with(RESERVE_STOCK_QUEUE);
  }

  @Bean
  public Binding processPaymentBinding(Queue processPaymentQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(processPaymentQueue).to(orderExchange).with(PROCESS_PAYMENT_QUEUE);
  }

  @Bean
  public Binding releaseStockBinding(Queue releaseStockQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(releaseStockQueue).to(orderExchange).with(RELEASE_STOCK_QUEUE);
  }

  @Bean
  public Binding refundPaymentBinding(Queue refundPaymentQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(refundPaymentQueue).to(orderExchange).with(REFUND_PAYMENT_QUEUE);
  }

  @Bean
  public Binding stockReservedBinding(Queue stockReservedQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(stockReservedQueue).to(orderExchange).with(STOCK_RESERVED_QUEUE);
  }

  @Bean
  public Binding paymentProcessedBinding(Queue paymentProcessedQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(paymentProcessedQueue)
        .to(orderExchange)
        .with(PAYMENT_PROCESSED_QUEUE);
  }

  @Bean
  public Binding orderReceivedBinding(Queue orderReceivedQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(orderReceivedQueue).to(orderExchange).with(ORDER_RECEIVED_QUEUE);
  }
}
