package io.github.v4runsharma.correlationid;

import org.slf4j.MDC;

public final class CorrelationIdHolder {

  private CorrelationIdHolder() {
  }

  public static void set(String mdcKey, String correlationId) {
    MDC.put(mdcKey, correlationId);
  }

  public static String get(String mdcKey) {
    return MDC.get(mdcKey);
  }

  public static void clear(String mdcKey) {
    MDC.remove(mdcKey);
  }
}
