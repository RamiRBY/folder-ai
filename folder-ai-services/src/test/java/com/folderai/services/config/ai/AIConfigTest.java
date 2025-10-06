package com.folderai.services.config.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the AIConfig class. This test verifies the bean creation logic in isolation
 * without loading a Spring context.
 */
@ExtendWith(MockitoExtension.class)
class AIConfigTest {

  @Mock
  private OpenAiChatModel mockOpenAiChatModel;

  @Mock
  private AnthropicChatModel mockAnthropicChatModel;

  @Mock
  private ChatMemory mockChatMemory;

  @InjectMocks
  private AIConfig aiConfig;

  @Test
  @DisplayName("inMemoryChatMemoryRepository should return a new instance")
  void inMemoryChatMemoryRepository_shouldReturnNewInstance() {
    // when
    InMemoryChatMemoryRepository repository = aiConfig.inMemoryChatMemoryRepository();

    // then
    assertThat(repository).isNotNull().isExactlyInstanceOf(InMemoryChatMemoryRepository.class);
  }

  @Test
  @DisplayName("chatMemory should create MessageWindowChatMemory with given repository")
  void chatMemory_shouldCreateMessageWindowChatMemory() {
    // given
    var repository = new InMemoryChatMemoryRepository();

    // when
    ChatMemory chatMemory = aiConfig.chatMemory(repository);

    // then
    assertThat(chatMemory).isNotNull().isInstanceOf(MessageWindowChatMemory.class);
  }

  @Test
  @DisplayName("openAiChatClient should build a non-null ChatClient")
  void openAiChatClient_shouldBuildClient() {
    // when
    ChatClient result = aiConfig.openAiChatClient(mockOpenAiChatModel, mockChatMemory);

    // then
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("anthropicChatClient should build a non-null ChatClient")
  void anthropicChatClient_shouldBuildClient() {
    // when
    ChatClient result = aiConfig.anthropicChatClient(mockAnthropicChatModel, mockChatMemory);

    // then
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("primaryChatClient should return the injected Anthropic client instance")
  void primaryChatClient_shouldReturnAnthropicClient() {
    // given
    // We can use a simple mock to represent the anthropicChatClient bean
    ChatClient mockAnthropicClient = ChatClient.builder(mockAnthropicChatModel).build();

    // when
    ChatClient result = aiConfig.chatClient(mockAnthropicClient);

    // then
    assertThat(result).isSameAs(mockAnthropicClient);
  }
}