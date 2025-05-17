package com.api.order.core.domain;

import static java.lang.String.format;

import com.api.order.core.domain.exception.DomainException;
import com.api.order.core.domain.valueobject.PaymentMethod;
import com.api.order.core.domain.valueobject.PaymentStatus;
import com.api.order.core.domain.valueobject.ValidationDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder(toBuilder = true)
public class PaymentDetails {

  private static final String DOMAIN_MESSAGE_ERROR = "by domain order";
  private static final String BLANK_MESSAGE_ERROR = "Field=[%s] should not be empty or null";

  private Integer id;
  private PaymentMethod paymentMethod;
  private String cardNumber;
  private PaymentStatus status;

  public PaymentDetails() {}

  public PaymentDetails(
      final Integer id,
      final PaymentMethod paymentMethod,
      final String cardNumber,
      final PaymentStatus status) {

    validateDomain(cardNumber);

    this.id = id;
    this.paymentMethod = paymentMethod;
    this.cardNumber = cardNumber;
    this.status = status;
  }

  public static PaymentDetails createPaymentDetails(
      final PaymentMethod paymentMethod, final String cardNumber) {
    return PaymentDetails.builder()
        .paymentMethod(paymentMethod)
        .cardNumber(cardNumber)
        .status(PaymentStatus.PENDING)
        .build();
  }

  public Integer getId() {
    return id;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public PaymentDetails changePaymentStatus(final PaymentStatus status) {
    return toBuilder().status(status).build();
  }

  private static void validateDomain(final String cardNumber) {
    final List<ValidationDomain<?>> rules =
        List.of(
            new ValidationDomain<>(
                cardNumber,
                format(BLANK_MESSAGE_ERROR, "card_number"),
                List.of(Objects::isNull, String::isBlank)));

    final var errors = validate(rules);

    if (!errors.isEmpty()) {
      throw new DomainException(errors);
    }
  }

  private static List<String> validate(final List<ValidationDomain<?>> validations) {
    return validations.stream()
        .filter(PaymentDetails::isInvalid)
        .map(it -> format("%s %s", it.message(), DOMAIN_MESSAGE_ERROR))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static <T> boolean isInvalid(final ValidationDomain<T> domain) {
    return domain.predicates().stream().anyMatch(predicate -> predicate.test(domain.field()));
  }
}
