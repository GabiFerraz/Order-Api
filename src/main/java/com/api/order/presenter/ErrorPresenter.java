package com.api.order.presenter;

import com.api.order.presenter.response.ErrorPresenterResponse;
import org.springframework.stereotype.Component;

@Component
public class ErrorPresenter {

  public ErrorPresenterResponse toPresenterErrorResponse(final String errorMessage) {
    return ErrorPresenterResponse.builder().errorMessage(errorMessage).build();
  }
}
