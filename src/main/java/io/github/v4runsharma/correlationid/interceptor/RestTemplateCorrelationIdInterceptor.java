package io.github.v4runsharma.correlationid.interceptor;

import io.github.v4runsharma.correlationid.CorrelationIdHolder;
import io.github.v4runsharma.correlationid.autoconfigure.CorrelationIdProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateCorrelationIdInterceptor implements ClientHttpRequestInterceptor {
  private final CorrelationIdProperties properties;

  public RestTemplateCorrelationIdInterceptor(CorrelationIdProperties properties) {
    this.properties = properties;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request,
                                      byte[] body,
                                      ClientHttpRequestExecution execution)
      throws IOException {

    String mdcKey = properties.getMdcKey();
    String headerName = properties.getHeaderName();

    String correlationId = CorrelationIdHolder.get(mdcKey);

    if (correlationId != null && !correlationId.isBlank()) {
      request.getHeaders().add(headerName, correlationId);
    }

    return execution.execute(request, body);
  }
}
