package com.api.order.infra.gateway;

import static java.lang.String.format;

import com.api.order.core.gateway.ProductApiGateway;
import com.api.order.infra.gateway.exception.GatewayException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductApiGatewayAdapter implements ProductApiGateway {

  @Value("${app.product-api.base-url}")
  private String productApiBaseUrl;

  private final WebClient.Builder webClientBuilder;

  @Override
  public BigDecimal getProductPrice(final String productSku) {
    try {
      final String url = productApiBaseUrl + "/" + productSku;

      return callService(url);
    } catch (Exception e) {
      throw new GatewayException(format("Failed to access Product API=[%s]", e.getMessage()));
    }
  }

  private BigDecimal callService(final String url) {
    WebClient webClient = webClientBuilder.baseUrl(url).build();

    return webClient.get().retrieve().bodyToMono(BigDecimal.class).block();
  }
}
