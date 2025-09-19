package com.folderai.services.exception;

import com.folderai.services.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * A global exception handler for the application. This class uses @ControllerAdvice to intercept
 * exceptions thrown from any controller.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles FolderGenerationException.
   *
   * @param exception The FolderGenerationException exception that was thrown.
   * @return A ResponseEntity containing a standardized error response.
   */
  @ExceptionHandler(value = FolderGenerationException.class, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ErrorResponse> handleFolderGenerationException(
      FolderGenerationException exception) {
    var errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Folder Generation Failed",
        exception.getMessage()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles PromptGenerationException
   *
   * @param exception The PromptGenerationException exception that was thrown.
   * @return A ResponseEntity containing a standardized error response.
   */
  @ExceptionHandler(PromptGenerationException.class)
  public ResponseEntity<ErrorResponse> handlePromptGenerationException(
      PromptGenerationException exception) {
    var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.name(), exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }


  /**
   * Handles FolderFileNotFoundException
   *
   * @param exception The FolderFileNotFoundException exception that was thrown.
   * @return A ResponseEntity containing a standardized error response.
   */
  @ExceptionHandler(FolderFileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFileNotFoundException(
      FolderFileNotFoundException exception) {
    var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.name(), exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

}
