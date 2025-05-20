package com.api.order.entrypoint.controller;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.api.order.core.domain.Order;
import com.api.order.core.domain.PaymentDetails;
import com.api.order.core.domain.valueobject.OrderStatus;
import com.api.order.core.domain.valueobject.PaymentMethod;
import com.api.order.core.domain.valueobject.PaymentStatus;
import com.api.order.core.dto.OrderDto;
import com.api.order.core.dto.PaymentDetailsDto;
import com.api.order.core.usecase.CreateOrder;
import com.api.order.core.usecase.DeleteOrder;
import com.api.order.core.usecase.SearchOrder;
import com.api.order.presenter.ErrorPresenter;
import com.api.order.presenter.OrderPresenter;
import com.api.order.presenter.response.OrderPresenterResponse;
import com.api.order.presenter.response.PaymentDetailsPresenterResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderControllerTest {

  private static final String BASE_URL = "/api/orders";
  private static final String BASE_URL_WITH_ID = BASE_URL + "/%s";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CreateOrder createOrder;
  @MockitoBean private SearchOrder searchOrder;
  @MockitoBean private DeleteOrder deleteOrder;
  @MockitoBean private OrderPresenter presenter;
  @MockitoBean private ErrorPresenter errorPresenter;

  @Test
  void shouldCreateOrderSuccessfully() throws Exception {
    final var request =
        OrderDto.builder()
            .productSku("BOLA-123-ABC")
            .productQuantity(10)
            .clientCpf("12345678901")
            .paymentDetails(new PaymentDetailsDto("CREDIT_CARD", "1234567890123456"))
            .build();
    final var response =
        new Order(
            "1",
            "BOLA-123-ABC",
            10,
            "12345678901",
            OrderStatus.OPEN,
            new PaymentDetails(
                1, PaymentMethod.CREDIT_CARD, "1234567890123456", PaymentStatus.PENDING),
            BigDecimal.valueOf(100),
            false);
    final var presenterResponse =
        new OrderPresenterResponse(
            response.getId(),
            response.getProductSku(),
            response.getProductQuantity(),
            response.getClientCpf(),
            response.getStatus().name(),
            PaymentDetailsPresenterResponse.builder()
                .id(response.getPaymentDetails().getId())
                .paymentMethod(response.getPaymentDetails().getPaymentMethod().name())
                .cardNumber(response.getPaymentDetails().getCardNumber())
                .status(response.getPaymentDetails().getStatus().name())
                .build());

    when(createOrder.execute(any(OrderDto.class))).thenReturn(response);
    when(presenter.parseToResponse(response)).thenReturn(presenterResponse);

    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(response.getId()))
        .andExpect(jsonPath("$.productSku").value(response.getProductSku()))
        .andExpect(jsonPath("$.productQuantity").value(response.getProductQuantity()))
        .andExpect(jsonPath("$.clientCpf").value(response.getClientCpf()))
        .andExpect(
            jsonPath("$.paymentDetails.paymentMethod")
                .value(response.getPaymentDetails().getPaymentMethod().name()))
        .andExpect(
            jsonPath("$.paymentDetails.cardNumber")
                .value(response.getPaymentDetails().getCardNumber()));
  }

  @Test
  void shouldSearchOrderSuccessfully() throws Exception {
    final var id = "1";
    final var response =
        new Order(
            "1",
            "BOLA-123-ABC",
            10,
            "12345678901",
            OrderStatus.OPEN,
            new PaymentDetails(
                1, PaymentMethod.CREDIT_CARD, "1234567890123456", PaymentStatus.PENDING),
            BigDecimal.valueOf(100),
            false);
    final var presenterResponse =
        new OrderPresenterResponse(
            response.getId(),
            response.getProductSku(),
            response.getProductQuantity(),
            response.getClientCpf(),
            response.getStatus().name(),
            PaymentDetailsPresenterResponse.builder()
                .id(response.getPaymentDetails().getId())
                .paymentMethod(response.getPaymentDetails().getPaymentMethod().name())
                .cardNumber(response.getPaymentDetails().getCardNumber())
                .status(response.getPaymentDetails().getStatus().name())
                .build());

    when(searchOrder.execute(id)).thenReturn(Optional.of(response));
    when(presenter.parseToResponse(response)).thenReturn(presenterResponse);

    mockMvc
        .perform(get(format(BASE_URL_WITH_ID, id)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()))
        .andExpect(jsonPath("$.productSku").value(response.getProductSku()))
        .andExpect(jsonPath("$.productQuantity").value(response.getProductQuantity()))
        .andExpect(jsonPath("$.clientCpf").value(response.getClientCpf()))
        .andExpect(
            jsonPath("$.paymentDetails.paymentMethod")
                .value(response.getPaymentDetails().getPaymentMethod().name()))
        .andExpect(
            jsonPath("$.paymentDetails.cardNumber")
                .value(response.getPaymentDetails().getCardNumber()));
  }

  @Test
  void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
    final var id = "order-123";

    when(searchOrder.execute(id)).thenReturn(Optional.empty());

    mockMvc.perform(get(format(BASE_URL_WITH_ID, id))).andExpect(status().isNotFound());
  }

  @Test
  void shouldDeleteOrderSuccessfully() throws Exception {
    final var id = "order-123";

    doNothing().when(deleteOrder).execute(id);

    mockMvc.perform(delete(format(BASE_URL_WITH_ID, id))).andExpect(status().isNoContent());
  }
}
