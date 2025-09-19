package com.folderai.services.config.filter;

import com.folderai.services.commun.FolderConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the MdcFilter. Verifies that the filter correctly manages the SLF4J Mapped
 * Diagnostic Context (MDC) for each request, especially ensuring cleanup even when exceptions
 * occur.
 */
@ExtendWith(MockitoExtension.class)
class MdcFilterTest {

  @Mock
  private ServletRequest mockRequest;

  @Mock
  private ServletResponse mockResponse;

  @Mock
  private FilterChain mockFilterChain;

  @InjectMocks
  private MdcFilter mdcFilter;

  @BeforeEach
  void setUp() {
    MDC.clear();
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  @DisplayName("doFilter should set traceId in MDC, proceed with chain, and clear MDC on successful request")
  void doFilter_shouldSetAndClearMdcOnSuccessfulRequest() throws IOException, ServletException {
    // Arrange
    // Use doAnswer to check the MDC state *during* the chain execution
    doAnswer(invocation -> {
      // Assert that the traceId is present while the chain is executing
      assertThat(MDC.get(FolderConstants.TRACE_ID_KEY)).isNotNull().isNotBlank();
      return null;
    }).when(mockFilterChain).doFilter(mockRequest, mockResponse);

    // Act
    mdcFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

    verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);

    assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
  }

  @Test
  @DisplayName("doFilter should clear MDC even if the filter chain throws an exception")
  void doFilter_shouldClearMdcEvenWhenChainThrowsException() throws IOException, ServletException {
    String errorMessage = "Something went wrong in the chain";
    doThrow(new ServletException(errorMessage)).when(mockFilterChain)
        .doFilter(mockRequest, mockResponse);

    assertThatThrownBy(() -> mdcFilter.doFilter(mockRequest, mockResponse, mockFilterChain))
        .isInstanceOf(ServletException.class)
        .hasMessage(errorMessage);

    assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
  }
}
