package com.api.order.entrypoint.controller;

import static java.lang.String.format;

import com.api.order.core.domain.Order;
import com.api.order.core.dto.OrderDto;
import com.api.order.core.dto.PaymentDetailsDto;
import com.api.order.core.usecase.CreateOrder;
import com.api.order.core.usecase.DeleteOrder;
import com.api.order.core.usecase.SearchOrder;
import com.api.order.presenter.ErrorPresenter;
import com.api.order.presenter.OrderPresenter;
import com.api.order.presenter.response.OrderPresenterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

  private static final String ORDER_NOT_FOUND_MESSAGE = "Order with id=[%s] not found.";

  private final CreateOrder createOrder;
  private final SearchOrder searchOrder;
  private final DeleteOrder deleteOrder;
  private final OrderPresenter presenter;
  private final ErrorPresenter errorPresenter;

  @PostMapping
  public ResponseEntity<OrderPresenterResponse> create(@Valid @RequestBody final Order request) {
    final var order = this.createOrder.execute(toOrderDto(request));

    return new ResponseEntity<>(this.presenter.parseToResponse(order), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> search(@Validated @PathVariable("id") final int id) {
    final var response = this.searchOrder.execute(id);

    return response
        .<ResponseEntity<Object>>map(order -> ResponseEntity.ok(presenter.parseToResponse(order)))
        .orElseGet(
            () ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        errorPresenter.toPresenterErrorResponse(
                            format(ORDER_NOT_FOUND_MESSAGE, id))));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@Validated @PathVariable("id") final int id) {
    this.deleteOrder.execute(id);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private OrderDto toOrderDto(final Order order) {
    final var payment = order.getPaymentDetails();
    final var paymentDto =
        new PaymentDetailsDto(payment.getPaymentMethod().name(), payment.getCardNumber());

    return OrderDto.builder()
        .productSku(order.getProductSku())
        .productQuantity(order.getProductQuantity())
        .clientCpf(order.getClientCpf())
        .paymentDetails(paymentDto)
        .build();
  }
}
