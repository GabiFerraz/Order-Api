package com.api.order.infra.persistence.repository;

import com.api.order.infra.persistence.entity.PaymentDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetailsEntity, Integer> {}
