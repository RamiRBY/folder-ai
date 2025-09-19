package com.folderai.services.config.filter;

import com.folderai.services.commun.FolderConstants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * A servlet filter that sets up the Mapped Diagnostic Context (MDC) for each request. This filter
 * is responsible for generating a unique traceId for each incoming request, making it possible to
 * correlate log entries for a single operation. It runs with the highest precedence to ensure the
 * MDC is available for all subsequent processing.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcFilter implements Filter {


  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      MDC.put(FolderConstants.TRACE_ID_KEY, UUID.randomUUID().toString());
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
