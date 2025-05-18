package com.api.order.infra.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String id;

  @Column(name = "product_sku", nullable = false)
  private String productSku;

  @Column(name = "product_quantity", nullable = false)
  private Integer productQuantity;

  @Column(name = "client_cpf", nullable = false)
  private String clientCpf;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "payment_details_id", referencedColumnName = "id", nullable = false)
  private PaymentDetailsEntity paymentDetail;

  @Column(name = "stock_reserved")
  private boolean stockReserved = false;
}
