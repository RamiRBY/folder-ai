package com.folderai.services.ai.prompt;

import com.folderai.services.config.properties.PromptProperties;
import com.folderai.services.exception.FolderFileNotFoundException;
import com.folderai.services.exception.PromptGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Comprehensive unit tests for the {@link PromptFactory} class. This suite verifies both successful
 * prompt creation and robust error handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PromptFactory Tests")
class PromptFactoryTest {

  @Mock
  private PromptProperties promptProperties;
  @Mock
  private ResourceLoader resourceLoader;

  @InjectMocks
  private PromptFactory promptFactory;

  // --- Test Constants ---
  private static final String FAKE_USER_PROMPT = "learn vue js.";
  private static final String FAKE_FORMAT_INSTRUCTIONS = "{\"format\":\"json\"}";

  private static final String DIRECTORY_SYSTEM_PATH = "classpath:/prompts/directory/system.st";
  private static final String DIRECTORY_USER_PATH = "classpath:/prompts/directory/user.st";


  /**
   * Sets up the mock behavior for PromptProperties before each test.
   */
  @BeforeEach
  void setUp() {
    var structureProps = new PromptProperties.Directory(DIRECTORY_SYSTEM_PATH, DIRECTORY_USER_PATH);
    lenient().when(promptProperties.directory()).thenReturn(structureProps);
  }

  /**
   * Helper method to mock the ResourceLoader's behavior for a given path and content.
   */
  private void mockResource(String path, String content) {
    Resource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
    when(resourceLoader.getResource(path)).thenReturn(resource);
  }

  @Nested
  @DisplayName("Success Scenarios")
  class SuccessScenarios {

    @Test
    @DisplayName("should create director prompt with correctly rendered templates")
    void createDirectorPrompt_Success() {
      // Arrange
      String systemTemplate = "System instructions with format: {format}";
      String userTemplate = "User request: {prompt}";
      mockResource(DIRECTORY_SYSTEM_PATH, systemTemplate);
      mockResource(DIRECTORY_USER_PATH, userTemplate);

      // Act
      Prompt result = promptFactory.createStructurePrompt(FAKE_USER_PROMPT,
          FAKE_FORMAT_INSTRUCTIONS);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getInstructions()).hasSize(2);
      assertThat(result.getInstructions().get(0)).isInstanceOf(UserMessage.class);
      assertThat(result.getInstructions().get(1)).isInstanceOf(UserMessage.class);

      // ** CRITICAL: Verify that the placeholders were correctly replaced **
      assertThat(result.getInstructions().get(0).getText()).contains(FAKE_FORMAT_INSTRUCTIONS);
      assertThat(result.getInstructions().get(1).getText()).contains(FAKE_USER_PROMPT);
    }


  }

  @Nested
  @DisplayName("Failure Scenarios")
  class FailureScenarios {

    @Test
    @DisplayName("should throw PromptGenerationException if system template not found")
    void createPrompt_SystemTemplateNotFound() {
      // Arrange: Mock the user resource but make the system one "not exist"
      mockResource(DIRECTORY_USER_PATH, "User template");
      Resource nonExistentResource = new ByteArrayResource(new byte[0]) {
        @Override
        public boolean exists() {
          return false;
        }
      };
      when(resourceLoader.getResource(DIRECTORY_USER_PATH)).thenReturn(nonExistentResource);

      // Act & Assert
      var exception = assertThrows(PromptGenerationException.class,
          () -> promptFactory.createStructurePrompt(FAKE_USER_PROMPT, FAKE_FORMAT_INSTRUCTIONS));

      assertThat(exception.getCause()).isInstanceOf(FolderFileNotFoundException.class);
      assertThat(exception.getCause().getCause()).isInstanceOf(IOException.class)
          .hasMessage("Resource not found at location: " + DIRECTORY_USER_PATH);
    }

  }
}
