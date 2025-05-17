package com.api.order.presenter;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.PaymentDetails;
import com.api.order.presenter.response.OrderPresenterResponse;
import com.api.order.presenter.response.PaymentDetailsPresenterResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderPresenter {

  public OrderPresenterResponse parseToResponse(final Order order) {
    return OrderPresenterResponse.builder()
        .id(order.getId())
        .productSku(order.getProductSku())
        .productQuantity(order.getProductQuantity())
        .clientCpf(order.getClientCpf())
        .status(order.getStatus().name())
        .paymentDetails(this.parseToResponse(order.getPaymentDetails()))
        .build();
  }

  private PaymentDetailsPresenterResponse parseToResponse(final PaymentDetails paymentDetails) {
    return PaymentDetailsPresenterResponse.builder()
        .id(paymentDetails.getId())
        .paymentMethod(paymentDetails.getPaymentMethod().name())
        .cardNumber(paymentDetails.getCardNumber())
        .status(paymentDetails.getStatus().name())
        .build();
  }
}
