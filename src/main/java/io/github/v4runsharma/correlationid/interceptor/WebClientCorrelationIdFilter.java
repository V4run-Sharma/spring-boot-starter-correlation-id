package io.github.v4runsharma.correlationid.interceptor;

import io.github.v4runsharma.correlationid.CorrelationIdHolder;
import io.github.v4runsharma.correlationid.autoconfigure.CorrelationIdProperties;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

public class WebClientCorrelationIdFilter {

  private final CorrelationIdProperties properties;

  public WebClientCorrelationIdFilter(CorrelationIdProperties properties) {
    this.properties = properties;
  }

  public ExchangeFilterFunction asExchangeFilterFunction() {
    return (request, next) -> {

      String correlationId =
          CorrelationIdHolder.get(properties.getMdcKey());

      if (correlationId == null || correlationId.isBlank()) {
        return next.exchange(request);
      }

      ClientRequest mutated = ClientRequest.from(request)
          .header(properties.getHeaderName(), correlationId)
          .build();

      return next.exchange(mutated);
    };
  }
}
