package com.api.order.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDetailsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "payment_method", nullable = false)
  private String paymentMethod;

  @Column(name = "card_number", nullable = false)
  private String cardNumber;

  @Column(name = "status", nullable = false)
  private String status;

  @OneToOne(mappedBy = "paymentDetail")
  private OrderEntity order;
}
