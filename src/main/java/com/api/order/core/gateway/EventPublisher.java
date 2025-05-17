package com.api.order.core.gateway;

public interface EventPublisher {

  void publish(final Object event);
}
