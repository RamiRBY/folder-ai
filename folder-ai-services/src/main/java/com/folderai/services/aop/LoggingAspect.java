package com.folderai.services.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folderai.services.commun.FolderConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Aspect for logging execution of service and controller methods. This aspect provides structured,
 * contextual logging for method entry, exit, execution time, and errors, using AOP to keep business
 * logic clean.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

  private final ObjectMapper objectMapper;

  /**
   * Pointcut that matches all public methods in the service package and its subpackages.
   */
  @Pointcut("execution(public * com.folderai.services.service..*.*(..))")
  public void serviceLayerPointcut() {
    // Method is empty as this is just a Pointcut definition.
  }

  /**
   * Pointcut that matches all public methods in the controller package.
   */
  @Pointcut("execution(public * com.folderai.services.controller..*.*(..))")
  public void controllerLayerPointcut() {
    // Method is empty as this is just a Pointcut definition.
  }

  /**
   * A combined pointcut for both service and controller layers.
   */
  @Pointcut("serviceLayerPointcut() || controllerLayerPointcut()")
  public void applicationLayerPointcut() {
    // Method is empty as this is just a Pointcut definition.
  }

  /**
   * Around advice that logs method execution. INFO level logs the flow, DEBUG level logs detailed
   * arguments and results.
   */
  @Around("applicationLayerPointcut()")
  public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
    MDC.put("className", joinPoint.getSignature().getDeclaringTypeName());
    MDC.put("methodName", joinPoint.getSignature().getName());

    String traceId = MDC.get(FolderConstants.TRACE_ID_KEY);
    if (traceId == null || traceId.isBlank()) {
      MDC.put(FolderConstants.TRACE_ID_KEY, UUID.randomUUID().toString());
    }

    log.info("Entering method");

    if (log.isDebugEnabled()) {
      log.debug("Method arguments: {}", serialize(joinPoint.getArgs()));
    }

    long startTime = System.currentTimeMillis();

    try {
      Object result = joinPoint.proceed();
      long elapsedTime = System.currentTimeMillis() - startTime;

      log.atInfo()
          .setMessage("Exiting method")
          .addKeyValue("executionTimeMs", elapsedTime)
          .log();

      // Log result only if DEBUG is enabled
      if (log.isDebugEnabled()) {
        log.debug("Method result: {}", serialize(result));
      }

      return result;

    } catch (Throwable throwable) {
      long elapsedTime = System.currentTimeMillis() - startTime;

      log.atError()
          .setMessage("Method threw exception")
          .addKeyValue("executionTimeMs", elapsedTime)
          .setCause(throwable)
          .log();

      throw throwable;
    } finally {
      MDC.clear();
    }
  }

  private String serialize(Object obj) {
    if (obj instanceof byte[]) {
      return String.format("<binary data of size %d bytes>", ((byte[]) obj).length);
    }
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      return "Failed to serialize object to JSON";
    }
  }
}

