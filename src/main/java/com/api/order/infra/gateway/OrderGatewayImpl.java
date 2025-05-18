package com.api.order.infra.gateway;

import static java.lang.String.format;

import com.api.order.core.domain.Order;
import com.api.order.core.gateway.OrderGateway;
import com.api.order.infra.gateway.exception.GatewayException;
import com.api.order.infra.persistence.repository.OrderRepository;
import com.api.order.mapper.OrderEntityMapper;
import com.api.order.mapper.PaymentDetailsEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderGatewayImpl implements OrderGateway {

  private static final String SAVE_ERROR_MESSAGE = "Error saving order for id=[%s].";
  private static final String FIND_ERROR_MESSAGE = "Order id=[%s] not found.";
  private static final String UPDATE_ERROR_MESSAGE = "Error updating order for id=[%s].";
  private static final String DELETE_ERROR_MESSAGE = "Error deleting order for id=[%s].";

  private final OrderRepository orderRepository;

  @Override
  @Transactional
  public Order save(final Order order) {
    try {
      final var orderEntity = OrderEntityMapper.toEntity(order);
      final var paymentDetailsEntity =
          PaymentDetailsEntityMapper.toEntity(order.getPaymentDetails());

      orderEntity.setPaymentDetail(paymentDetailsEntity);
      paymentDetailsEntity.setOrder(orderEntity);

      final var savedResponse = orderRepository.save(orderEntity);

      final var savedOrder =
          OrderEntityMapper.toDomain(savedResponse).toBuilder().id(savedResponse.getId()).build();
      final var savedPaymentDetails =
          PaymentDetailsEntityMapper.toDomain(savedResponse.getPaymentDetail()).toBuilder()
              .id(savedResponse.getPaymentDetail().getId())
              .build();

      return savedOrder.toBuilder().paymentDetails(savedPaymentDetails).build();
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(SAVE_ERROR_MESSAGE, order.getId()));
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> findById(final String id) {
    try {
      return orderRepository
          .findById(id)
          .map(
              orderEntity -> {
                final var order = OrderEntityMapper.toDomain(orderEntity);
                final var paymentDetails =
                    PaymentDetailsEntityMapper.toDomain(orderEntity.getPaymentDetail()).toBuilder()
                        .id(orderEntity.getPaymentDetail().getId())
                        .build();
                return order.toBuilder().paymentDetails(paymentDetails).build();
              });
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(FIND_ERROR_MESSAGE, id));
    }
  }

  @Override
  @Transactional
  public Order update(final Order order) {
    try {
      log.info(
          "Updating order: id={}, stockReserved={}, paymentStatus={}, orderStatus={}",
          order.getId(),
          order.isStockReserved(),
          order.getPaymentDetails().getStatus(),
          order.getStatus());
      final var entity =
          orderRepository
              .findById(order.getId())
              .orElseThrow(() -> new GatewayException(format(FIND_ERROR_MESSAGE, order.getId())));
      log.info(
          "Entity before save: id={}, stockReserved={}", entity.getId(), entity.isStockReserved());

      entity.setStatus(order.getStatus().name());
      entity.getPaymentDetail().setStatus(order.getPaymentDetails().getStatus().name());
      entity.setStockReserved(order.isStockReserved());
      log.info(
          "Updating order entity id={}, stockReserved={}, orderStatus={}, paymentStatus: {}",
          entity.getId(),
          entity.isStockReserved(),
          entity.getStatus(),
          entity.getPaymentDetail().getStatus());

      final var savedResponse = orderRepository.saveAndFlush(entity);
      log.info(
          "Updated order entity id={}, stockReserved={}, orderStatus={}, paymentStatus: {}",
          savedResponse.getId(),
          savedResponse.isStockReserved(),
          savedResponse.getStatus(),
          savedResponse.getPaymentDetail().getStatus());

      return OrderEntityMapper.toDomain(savedResponse);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(UPDATE_ERROR_MESSAGE, order.getId()));
    }
  }

  @Override
  @Transactional
  public void delete(final String id) {
    try {
      orderRepository.deleteById(id);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(DELETE_ERROR_MESSAGE, id));
    }
  }
}
