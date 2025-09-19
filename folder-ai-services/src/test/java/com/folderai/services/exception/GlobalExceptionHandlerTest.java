package com.folderai.services.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the {@link GlobalExceptionHandler}.
 * <p>
 * This test class uses @WebMvcTest to load only the web layer, making it fast and focused. It
 * includes a dummy TestController to throw exceptions that the handler is expected to catch.
 */
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {GlobalExceptionHandler.class,
    GlobalExceptionHandlerTest.TestController.class})
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  private final static IOException mockEx = new IOException("IOException");

  @RestController
  static class TestController {

    @GetMapping("/test/folder-generation-exception")
    public void throwFolderGenerationException(@RequestParam String message) {
      throw new FolderGenerationException(message, mockEx);
    }

    @GetMapping("/test/prompt-not-found-exception")
    public void throwPromptGenerationException(@RequestParam String message) {
      throw new PromptGenerationException(message, new IOException());
    }


    @GetMapping("/test/file-not-found-exception")
    public void throwFolderFileNotFoundException(@RequestParam String message) {
      throw new FolderFileNotFoundException(message);
    }
  }

  @Test
  @DisplayName("Should handle FolderGenerationException and return 500 INTERNAL_SERVER_ERROR")
  void whenFolderGenerationExceptionIsThrown_thenReturns500InternalServerError()
      throws Exception {
    // Arrange
    final String errorMessage = "Failed to process template";
    final String url = "/test/folder-generation-exception";

    // Act & Assert
    // Act & Assert
    mockMvc.perform(get(url).param("message", errorMessage))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        // THE FIX: Changed "statusCode" to "status" to match the actual DTO field name.
        .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .andExpect(jsonPath("$.error").value("Folder Generation Failed"))
        .andExpect(jsonPath("$.message").value(errorMessage))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  @DisplayName("Should handle PromptNotFoundException and return 404 NOT_FOUND")
  void whenPromptNotFoundExceptionIsThrown_thenReturns404NotFound() throws Exception {
    // Arrange
    final String errorMessage = "Prompt with ID 123 not found";
    final String url = "/test/prompt-not-found-exception";

    // Act & Assert
    mockMvc.perform(get(url).param("message", errorMessage))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.error").value("NOT_FOUND"))
        .andExpect(jsonPath("$.message").value(errorMessage))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  @DisplayName("Should handle FolderFileNotFoundException and return 404 NOT_FOUND")
  void whenFolderFileNotFoundExceptionIsThrown_thenReturns404NotFound() throws Exception {
    // Arrange
    final String errorMessage = "Template file 'pom.xml.template' not found";
    final String url = "/test/file-not-found-exception";

    // Act & Assert
    // This test will fail with the original code due to the incorrect exception type in the handler method.
    // It will pass once the method signature is corrected to:
    // handleFileNotFoundException(FolderFileNotFoundException exception)
    mockMvc.perform(get(url).param("message", errorMessage))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.error").value("NOT_FOUND"))
        .andExpect(jsonPath("$.message").value(errorMessage))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Nested
  @DisplayName("GlobalExceptionHandler object instantiation test")
  public class GlobalExceptionHandlerInitTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("test FolderFileNotFoundException")
    public void shouldInitFolderFileNotFoundException() {
      var msg = "test message";
      var ex =
          globalExceptionHandler.handleFileNotFoundException(new FolderFileNotFoundException(
              "test message"));
      Assertions.assertNotNull(ex.getBody());
      var exBody = ex.getBody();
      assertEquals(msg, exBody.message());
      assertEquals(HttpStatus.NOT_FOUND.name(), exBody.error());
    }


    @Test
    @DisplayName("test FolderGenerationException")
    public void shouldInitFolderGenerationException() {
      var msg = "test message";
      var ex =
          globalExceptionHandler.handleFolderGenerationException(new FolderGenerationException(
              "test message", new IOException()));
      Assertions.assertNotNull(ex.getBody());
      var exBody = ex.getBody();
      assertEquals(msg, exBody.message());
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exBody.status());
    }


  }
}
