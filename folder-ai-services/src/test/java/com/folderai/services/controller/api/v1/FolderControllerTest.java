package com.folderai.services.controller.api.v1;

import com.folderai.services.dto.request.FolderRequest;
import com.folderai.services.dto.response.DirectoryStructure;
import com.folderai.services.dto.response.FolderResponse;
import com.folderai.services.exception.FolderGenerationException;
import com.folderai.services.service.FolderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the FolderController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FolderController test class")
class FolderControllerTest {

  @Mock
  private FolderService folderService;

  @InjectMocks
  private FolderController folderController;

  @Nested
  @DisplayName("POST /api/v1/folders/project-directory")
  class GenerateArchitectFolderTests {

    @Test
    @DisplayName("should return 200 OK")
    void shouldReturnOkWithMultipartResponse() {
      // Arrange
      var request = new FolderRequest("Create a Java REST API", null, null);
      var mockResult = new FolderResponse(new DirectoryStructure("java-learn", "learning "
          + "directory"), null, null);

      when(folderService.generateProjectDirectory(any(FolderRequest.class))).thenReturn(mockResult);

      var response = folderController.generateProjectDirectory(request);

      assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("should return 500 Internal Server Error when service throws IOException")
    void shouldReturn500OnServiceError() {
      // Arrange
      var request = new FolderRequest("Create a Java REST API", null, null);
      when(folderService.generateProjectDirectory(any(FolderRequest.class))).thenThrow(
          new FolderGenerationException("Failed to create directory", new IOException()));

      // Act & Assert
      // In a unit test, we assert that the controller correctly propagates the exception.
      // The @ControllerAdvice is not active here.
      var exception = assertThrows(
          FolderGenerationException.class,
          () -> folderController.generateProjectDirectory(request)
      );
      assertThat(exception.getMessage()).isEqualTo("Failed to create directory");
    }
  }

}

