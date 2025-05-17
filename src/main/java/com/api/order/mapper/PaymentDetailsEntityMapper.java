package com.api.order.mapper;

import com.api.order.core.domain.PaymentDetails;
import com.api.order.core.domain.valueobject.PaymentMethod;
import com.api.order.core.domain.valueobject.PaymentStatus;
import com.api.order.infra.persistence.entity.PaymentDetailsEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentDetailsEntityMapper {

  public static PaymentDetailsEntity toEntity(final PaymentDetails domain) {
    return PaymentDetailsEntity.builder()
        .paymentMethod(domain.getPaymentMethod().name())
        .cardNumber(domain.getCardNumber())
        .status(domain.getStatus().name())
        .build();
  }

  public static PaymentDetails toDomain(final PaymentDetailsEntity entity) {
    return PaymentDetails.builder()
        .id(entity.getId())
        .paymentMethod(PaymentMethod.valueOf(entity.getPaymentMethod()))
        .cardNumber(entity.getCardNumber())
        .status(PaymentStatus.fromName(entity.getStatus()))
        .build();
  }
}
