package io.github.v4runsharma.correlationid.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "correlation-id")
public class CorrelationIdProperties {

  /**
   * HTTP header name for correlation id.
   */
  private String headerName = "X-Correlation-Id";

  /**
   * Whether to generate a correlation id if missing.
   */
  private boolean generateIfMissing = true;

  /**
   * MDC key to store correlation id.
   */
  private String mdcKey = "correlationId";

  public String getHeaderName() {
    return headerName;
  }

  public void setHeaderName(String headerName) {
    this.headerName = headerName;
  }

  public boolean isGenerateIfMissing() {
    return generateIfMissing;
  }

  public void setGenerateIfMissing(boolean generateIfMissing) {
    this.generateIfMissing = generateIfMissing;
  }

  public String getMdcKey() {
    return mdcKey;
  }

  public void setMdcKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }
}
