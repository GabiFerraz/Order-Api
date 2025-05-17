package com.api.order.mapper;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.PaymentDetails;
import com.api.order.core.domain.valueobject.OrderStatus;
import com.api.order.infra.persistence.entity.OrderEntity;
import com.api.order.infra.persistence.entity.PaymentDetailsEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderEntityMapper {

  public static OrderEntity toEntity(final Order domain) {
    final PaymentDetails paymentDetails = domain.getPaymentDetails();
    final PaymentDetailsEntity paymentDetailsEntity =
        PaymentDetailsEntityMapper.toEntity(paymentDetails);

    final var orderEntity =
        OrderEntity.builder()
            .productSku(domain.getProductSku())
            .productQuantity(domain.getProductQuantity())
            .clientCpf(domain.getClientCpf())
            .status(domain.getStatus().name())
            .totalAmount(domain.getTotalAmount())
            .build();

    orderEntity.setPaymentDetail(paymentDetailsEntity);
    paymentDetailsEntity.setOrder(orderEntity);

    return orderEntity;
  }

  public static Order toDomain(final OrderEntity entity) {
    final PaymentDetailsEntity paymentDetailsEntity = entity.getPaymentDetail();
    final PaymentDetails paymentDetails = PaymentDetailsEntityMapper.toDomain(paymentDetailsEntity);

    return Order.builder()
        .id(entity.getId())
        .productSku(entity.getProductSku())
        .productQuantity(entity.getProductQuantity())
        .clientCpf(entity.getClientCpf())
        .status(OrderStatus.fromName(entity.getStatus()))
        .totalAmount(entity.getTotalAmount())
        .paymentDetails(paymentDetails)
        .build();
  }
}
