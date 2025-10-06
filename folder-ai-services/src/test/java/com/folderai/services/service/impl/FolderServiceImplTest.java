package com.folderai.services.service.impl;

import com.folderai.services.ai.prompt.PromptFactory;
import com.folderai.services.common.enumeration.AIProvider;
import com.folderai.services.dto.request.FolderModelOptions;
import com.folderai.services.dto.request.FolderRequest;
import com.folderai.services.dto.response.DirectoryStructure;
import com.folderai.services.exception.FolderGenerationException;
import com.folderai.services.service.ai.AIProviderService;
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
  private AIProviderService aiProviderService;

  @Mock
  private ChatMemory chatMemory;

  @Mock
  private PromptFactory promptFactory;

  @InjectMocks
  private FolderServiceImpl folderService;
  // A mock client that the AIProviderService will return
  @Mock
  private ChatClient mockSelectedChatClient;

  @Mock
  private ChatClient.ChatClientRequestSpec requestSpec;
  @Mock
  private ChatClient.CallResponseSpec responseSpec;

  @Mock
  private ChatResponse chatResponse;


  @Nested
  @DisplayName("generateProjectDirectory Tests")
  class GenerateProjectDirectoryTests {

    private FolderRequest mockRequestWithProvider;
    private FolderRequest mockRequestWithoutProvider;

    private Prompt promptMock;
    private final UUID conversationId = UUID.fromString("63035339-877c-432e-81a8-e1afb9c0d89b");

    @BeforeEach
    void setUp() {
      promptMock = mock(Prompt.class);

      // --- Updated request DTO to include the new 'provider' field ---
      mockRequestWithProvider = new FolderRequest(
          "A test project",
          conversationId,
          new FolderModelOptions("gpt-4o", 0.5),
          AIProvider.OPENAI // Explicitly set the provider
      );

      mockRequestWithoutProvider = new FolderRequest(
          "A default project",
          conversationId,
          new FolderModelOptions("claude-3", 0.7),
          null // Test the default provider case
      );

      // Mock the provider service to return our mock client
      when(aiProviderService.getChatClient(any(AIProvider.class))).thenReturn(
          mockSelectedChatClient);
      when(aiProviderService.getDefaultChatClient()).thenReturn(mockSelectedChatClient);
      when(requestSpec.options(any())).thenReturn(requestSpec);
      when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
      when(requestSpec.call()).thenReturn(responseSpec);
      when(responseSpec.chatResponse()).thenReturn(chatResponse);

    }

    @Test
    @DisplayName("should successfully generate a project when AI call is successful and use specific provider when one is requested")
    void generateProjectDirectory_whenProviderSpecified_whenSuccessful_thenReturnsGeneratedProject() {
      when(promptFactory.createStructurePrompt(anyString(), anyString())).thenReturn(
          promptMock);
      var aiJsonOutput = "{\"projectName\":\"test-project\",\"tree\":\"tree\"}";

      var assistantMessage = new AssistantMessage(aiJsonOutput);
      var generation = new Generation(assistantMessage);
      when(chatResponse.getResult()).thenReturn(generation);
      when(chatResponse.getMetadata()).thenReturn(
          ChatResponseMetadata.builder().model("gpt 4").build());

      var result = folderService.generateProjectDirectory(mockRequestWithProvider);

      assertThat(result).isNotNull();
      assertThat(result.directoryStructure().projectName()).isEqualTo("test-project");

      // --- VERIFY THE NEW LOGIC ---
      verify(aiProviderService).getChatClient(AIProvider.OPENAI);
      verify(aiProviderService, never()).getDefaultChatClient();
      verify(mockSelectedChatClient).prompt(any(Prompt.class));

    }

    @Test
    @DisplayName("should successfully generate a project when AI call is successful and use default provider when none is requested")
    void generateProjectDirectory_whenProviderIsNull_whenSuccessful_thenReturnsGeneratedProject() {
      when(promptFactory.createStructurePrompt(anyString(), anyString())).thenReturn(
          promptMock);
      var aiJsonOutput = "{\"projectName\":\"test-project\",\"tree\":\"tree\"}";

      var assistantMessage = new AssistantMessage(aiJsonOutput);
      var generation = new Generation(assistantMessage);
      when(chatResponse.getResult()).thenReturn(generation);
      when(chatResponse.getMetadata()).thenReturn(
          ChatResponseMetadata.builder().model("gpt 4").build());

      var result = folderService.generateProjectDirectory(mockRequestWithoutProvider);

      assertThat(result).isNotNull();
      assertThat(result.directoryStructure().projectName()).isEqualTo("default-project");

      // --- VERIFY THE NEW LOGIC ---
      verify(aiProviderService, never()).getChatClient(any(AIProvider.class));
      verify(aiProviderService).getDefaultChatClient();
      verify(mockSelectedChatClient).prompt(any(Prompt.class));

    }


    @Test
    @DisplayName("should throw FolderGenerationException when AI call fails")
    void generateProjectDirectory_whenAiFails_thenThrowsFolderGenerationException() {
      // Arrange
      when(promptFactory.createStructurePrompt(any(), any())).thenReturn(
          new Prompt("test"));
      when(promptFactory.createStructurePrompt(anyString(), anyString())).thenReturn(
          promptMock);

      when(responseSpec.chatResponse())
          .thenThrow(new NonTransientAiException("Current quota was exceeded."));

      // Act & Assert
      var exception = assertThrows(FolderGenerationException.class, () ->
          folderService.generateProjectDirectory(mockRequestWithProvider)
      );

      // Verify the specific error message for quota issues
      assertThat(exception.getMessage()).isEqualTo("Current quota was exceeded.");

    }

    @Test
    @DisplayName("should throw FolderGenerationException for other exceptions")
    void generateProjectDirectory_whenOtherExceptionOccurs_thenThrowsFolderGenerationException() {
      when(promptFactory.createStructurePrompt(any(), any())).thenReturn(
          new Prompt("test"));

      when(responseSpec.entity(any(BeanOutputConverter.class))).thenReturn(new DirectoryStructure(
          "test-project",
          "tree"
      ));
      var exception = assertThrows(FolderGenerationException.class, () ->
          folderService.generateProjectDirectory(mockRequestWithProvider)
      );

      assertThat(exception.getMessage()).isEqualTo(
          "Error during folder generation");
    }


  }

}

