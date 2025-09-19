package com.folderai.services.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.folderai.services.commun.FolderConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("LoggingAspect tests")
class LoggingAspectTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final LoggingAspect aspect = new LoggingAspect(objectMapper);
  private final ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
  private final Signature sig = mock(Signature.class);

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  @Test
  @DisplayName("logMethodExecution should Return Result")
  void logMethodExecution_shouldReturnResult() throws Throwable {
    // given
    when(sig.getDeclaringTypeName()).thenReturn("com.example.MyService");
    when(sig.getName()).thenReturn("myMethod");
    when(pjp.getSignature()).thenReturn(sig);
    when(pjp.getArgs()).thenReturn(new Object[]{"arg1"});
    when(pjp.proceed()).thenReturn("result");

    // when
    var result = aspect.logMethodExecution(pjp);

    // then
    assertThat(result).isEqualTo("result");
    MDC.getMDCAdapter().get("result");
  }

  @Test
  @DisplayName("logMethodExecution should Return Exception")
  void logMethodExecution_shouldRethrowException_andStillClearMdc() throws Throwable {
    when(sig.getDeclaringTypeName()).thenReturn("com.example.MyService");
    when(sig.getName()).thenReturn("errorMethod");
    when(pjp.getSignature()).thenReturn(sig);
    when(pjp.getArgs()).thenReturn(new Object[0]);
    when(pjp.proceed()).thenThrow(new IllegalStateException("boom"));

    assertThrows(IllegalStateException.class, () -> aspect.logMethodExecution(pjp));
    // After exception MDC should be cleared
    assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
  }

  @Test
  @DisplayName("MDC values are set OK")
  void mdcValuesAreSetDuringExecution() throws Throwable {
    when(sig.getDeclaringTypeName()).thenReturn("com.example.MyService");
    when(sig.getName()).thenReturn("myMethod");
    when(pjp.getSignature()).thenReturn(sig);
    when(pjp.getArgs()).thenReturn(new Object[0]);

    when(pjp.proceed()).thenAnswer(invocation -> {
      // This runs *before* the finally block clears MDC
      assertThat(MDC.get("className")).isEqualTo("com.example.MyService");
      assertThat(MDC.get("methodName")).isEqualTo("myMethod");
      assertThat(MDC.get(FolderConstants.TRACE_ID_KEY)).isNotBlank();
      return "ok";
    });

    var result = aspect.logMethodExecution(pjp);
    assertThat(result).isEqualTo("ok");
    assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
  }


}
