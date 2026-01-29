package io.github.v4runsharma.correlationid.filter;

import io.github.v4runsharma.correlationid.CorrelationIdHolder;
import io.github.v4runsharma.correlationid.autoconfigure.CorrelationIdProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class CorrelationIdFilter extends OncePerRequestFilter {

  private final CorrelationIdProperties properties;

  public CorrelationIdFilter(CorrelationIdProperties properties) {
    this.properties = properties;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String headerName = properties.getHeaderName();
    String mdcKey = properties.getMdcKey();

    String correlationId = request.getHeader(headerName);

    if (correlationId == null || correlationId.trim().isEmpty()) {
      if (properties.isGenerateIfMissing()) {
        correlationId = UUID.randomUUID().toString();
      }
    }

    if (correlationId != null) {
      CorrelationIdHolder.set(mdcKey, correlationId);
      response.setHeader(headerName, correlationId);
    }

    try {
      filterChain.doFilter(request, response);
    } finally {
      CorrelationIdHolder.clear(mdcKey);
    }
  }
}
