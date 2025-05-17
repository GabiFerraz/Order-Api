package com.api.order.core.domain;

import static java.lang.String.format;

import com.api.order.core.domain.exception.DomainException;
import com.api.order.core.domain.valueobject.OrderStatus;
import com.api.order.core.domain.valueobject.PaymentStatus;
import com.api.order.core.domain.valueobject.ValidationDomain;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder(toBuilder = true)
public class Order {

  private static final String DOMAIN_MESSAGE_ERROR = "by domain order";
  private static final String BLANK_MESSAGE_ERROR = "Field=[%s] should not be empty or null";
  private static final String PATTERN_ERROR_MESSAGE =
      "The field=[%s] is null or has an invalid pattern";
  private static final String NEGATIVE_MESSAGE_ERROR = "Field=[%s] should be greater than zero";
  private static final Predicate<String> PATTERN_SKU =
      n -> n == null || !Pattern.compile("^[A-Z0-9\\-]{5,20}$").matcher(n).matches();
  private static final Predicate<String> PATTERN_CPF =
      n -> n == null || !Pattern.compile("\\d{11}").matcher(n).matches();

  private String id;
  private String productSku;
  private Integer productQuantity;
  private String clientCpf;
  private OrderStatus status;
  private PaymentDetails paymentDetails;
  private BigDecimal totalAmount;

  public Order() {}

  public Order(
      final String id,
      final String productSku,
      final Integer productQuantity,
      final String clientCpf,
      final OrderStatus status,
      final PaymentDetails paymentDetails,
      final BigDecimal totalAmount) {

    validateDomain(productSku, productQuantity, clientCpf, totalAmount);

    this.id = id;
    this.productSku = productSku;
    this.productQuantity = productQuantity;
    this.clientCpf = clientCpf;
    this.status = status;
    this.paymentDetails = paymentDetails;
    this.totalAmount = totalAmount;
  }

  public static Order createOrder(
      final String productSku,
      final Integer productQuantity,
      final String clientCpf,
      final PaymentDetails paymentDetails,
      final BigDecimal unitPrice) {

    validateDomain(productSku, productQuantity, clientCpf, null);

    final var totalAmount = unitPrice.multiply(BigDecimal.valueOf(productQuantity));

    return Order.builder()
        .productSku(productSku)
        .productQuantity(productQuantity)
        .clientCpf(clientCpf)
        .status(OrderStatus.OPEN)
        .paymentDetails(paymentDetails)
        .totalAmount(totalAmount)
        .build();
  }

  public String getId() {
    return id;
  }

  public String getProductSku() {
    return productSku;
  }

  public Integer getProductQuantity() {
    return productQuantity;
  }

  public String getClientCpf() {
    return clientCpf;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public PaymentDetails getPaymentDetails() {
    return paymentDetails;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public Order changeOrderStatus(final OrderStatus status) {
    return toBuilder().status(status).build();
  }

  public Order updatePaymentStatus(final PaymentStatus paymentStatus) {
    return toBuilder().paymentDetails(paymentDetails.changePaymentStatus(paymentStatus)).build();
  }

  private static void validateDomain(
      final String productSku,
      final Integer productQuantity,
      final String clientCpf,
      final BigDecimal totalAmount) {
    final List<ValidationDomain<?>> rules =
        new ArrayList<>(
            List.of(
                new ValidationDomain<>(
                    productSku,
                    format(BLANK_MESSAGE_ERROR, "product_sku"),
                    List.of(Objects::isNull, String::isBlank)),
                new ValidationDomain<>(
                    productSku, format(PATTERN_ERROR_MESSAGE, "product_sku"), List.of(PATTERN_SKU)),
                new ValidationDomain<>(
                    productQuantity,
                    format(BLANK_MESSAGE_ERROR, "product_quantity"),
                    List.of(Objects::isNull)),
                new ValidationDomain<>(
                    productQuantity,
                    format(NEGATIVE_MESSAGE_ERROR, "product_quantity"),
                    List.of(q -> q != null && q <= 0)),
                new ValidationDomain<>(
                    clientCpf,
                    format(BLANK_MESSAGE_ERROR, "client_cpf"),
                    List.of(Objects::isNull, String::isBlank)),
                new ValidationDomain<>(
                    clientCpf, format(PATTERN_ERROR_MESSAGE, "client_cpf"), List.of(PATTERN_CPF))));

    if (totalAmount != null) {
      rules.add(
          new ValidationDomain<>(
              totalAmount,
              String.format(NEGATIVE_MESSAGE_ERROR, "total_amount"),
              List.of(t -> t != null && t.compareTo(BigDecimal.ZERO) <= 0)));
    }

    final var errors = validate(rules);

    if (!errors.isEmpty()) {
      throw new DomainException(errors);
    }
  }

  private static List<String> validate(final List<ValidationDomain<?>> validations) {
    return validations.stream()
        .filter(Order::isInvalid)
        .map(it -> format("%s %s", it.message(), DOMAIN_MESSAGE_ERROR))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static <T> boolean isInvalid(final ValidationDomain<T> domain) {
    return domain.predicates().stream().anyMatch(predicate -> predicate.test(domain.field()));
  }
}
