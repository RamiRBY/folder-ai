package com.folderai.services.exception;

/**
 * Custom exception to be thrown when fail to generate a Spring Ai Prompt Object.
 */
public class PromptGenerationException extends RuntimeException {

  public PromptGenerationException(String message, Throwable cause) {
    super(message, cause);
  }

}
