package com.folderai.services.service.impl;

import com.folderai.services.ai.prompt.PromptFactory;
import com.folderai.services.dto.request.FolderModelOptions;
import com.folderai.services.dto.request.FolderRequest;
import com.folderai.services.dto.response.DirectoryStructure;
import com.folderai.services.exception.FolderGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.retry.NonTransientAiException;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("FolderServiceImpl Tests")
class FolderServiceImplTest {

  @Mock
  private ChatClient chatClient;

  @Mock
  private ChatMemory chatMemory;

  @Mock
  private PromptFactory promptFactory;
  @Mock
  private ChatClient.ChatClientRequestSpec requestSpec;
  @Mock
  private ChatClient.CallResponseSpec responseSpec;

  @Mock
  private ChatResponse chatResponse;


  @InjectMocks
  private FolderServiceImpl folderService;


  @Nested
  @DisplayName("generateProjectDirectory Tests")
  class GenerateProjectDirectoryTests {

    private FolderRequest mockRequest;
    private Prompt promptMock;
    private final UUID conversationId = UUID.fromString("63035339-877c-432e-81a8-e1afb9c0d89b");

    @BeforeEach
    void setUp() {
      promptMock = mock(Prompt.class);

      mockRequest = new FolderRequest(
          "A test project",
          conversationId
          ,
          new FolderModelOptions("gpt-4o", 0.5)
      );

      when(chatClient.prompt(promptMock)).thenReturn(requestSpec);
      when(requestSpec.options(any())).thenReturn(requestSpec);
      when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
      when(requestSpec.call()).thenReturn(responseSpec);
      when(responseSpec.chatResponse()).thenReturn(chatResponse);

    }

    @Test
    @DisplayName("should successfully generate a project when AI call is successful")
    void generateProjectDirectory_whenSuccessful_thenReturnsGeneratedProject() {
      when(promptFactory.createStructurePrompt(anyString(), anyString())).thenReturn(
          promptMock);
      var aiJsonOutput = "{\"projectName\":\"test-project\",\"tree\":\"tree\"}";

      var assistantMessage = new AssistantMessage(aiJsonOutput);
      var generation = new Generation(assistantMessage);
      when(chatResponse.getResult()).thenReturn(generation);
      when(chatResponse.getMetadata()).thenReturn(
          ChatResponseMetadata.builder().model("gpt 4").build());

      var result = folderService.generateProjectDirectory(mockRequest);

      assertThat(result).isNotNull();
      assertThat(result.directoryStructure().projectName()).isEqualTo("test-project");
      assertThat(result.directoryStructure().tree()).isEqualTo("tree");
      assertThat(result.conversationId()).isInstanceOf(UUID.class);
      assertThat(result.metaData().model()).isEqualTo("gpt 4");

      verify(promptFactory).createStructurePrompt(eq(mockRequest.prompt()),
          anyString());
      verify(chatClient).prompt(any(Prompt.class));

    }

    @Test
    @DisplayName("should throw ScaffoldGenerationException when AI call fails")
    void generateProjectDirectory_whenAiFails_thenThrowsScaffoldGenerationException() {
      // Arrange
      when(promptFactory.createStructurePrompt(any(), any())).thenReturn(
          new Prompt("test"));
      when(promptFactory.createStructurePrompt(anyString(), anyString())).thenReturn(
          promptMock);

      when(responseSpec.chatResponse())
          .thenThrow(new NonTransientAiException("Current quota was exceeded."));

      // Act & Assert
      var exception = assertThrows(FolderGenerationException.class, () ->
          folderService.generateProjectDirectory(mockRequest)
      );

      // Verify the specific error message for quota issues
      assertThat(exception.getMessage()).isEqualTo("Current quota was exceeded.");

    }

    @Test
    @DisplayName("should throw ScaffoldGenerationException for other exceptions")
    void generateProjectDirectory_whenOtherExceptionOccurs_thenThrowsScaffoldGenerationException() {
      when(promptFactory.createStructurePrompt(any(), any())).thenReturn(
          new Prompt("test"));

      when(responseSpec.entity(any(BeanOutputConverter.class))).thenReturn(new DirectoryStructure(
          "test-project",
          "tree"
      ));
      var exception = assertThrows(FolderGenerationException.class, () ->
          folderService.generateProjectDirectory(mockRequest)
      );

      assertThat(exception.getMessage()).isEqualTo(
          "Error during folder generation");
    }


  }

}

