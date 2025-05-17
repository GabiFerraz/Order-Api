package com.api.order.presenter.response;

import lombok.Builder;

@Builder
public record ErrorPresenterResponse(String errorMessage) {}
