package com.api.order.entrypoint.config;

import java.util.List;

public record ErrorMethodValidationResponse(String errorCode, List<String> violations) {}
