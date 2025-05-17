package com.api.order.core.gateway;

import com.api.order.core.domain.Order;
import java.util.Optional;

public interface OrderGateway {

  Order save(final Order order);

  Optional<Order> findById(final String id);

  Order update(final Order order);

  void delete(final String id);
}
