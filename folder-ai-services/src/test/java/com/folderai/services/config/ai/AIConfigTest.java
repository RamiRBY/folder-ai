package com.folderai.services.config.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AIConfig class. This test verifies the bean creation logic in isolation
 * without loading a Spring context.
 */
@ExtendWith(MockitoExtension.class)
class AIConfigTest {

  @Mock
  private ChatClient.Builder mockChatClientBuilder;

  @Mock
  private ChatClient mockChatClient;

  @InjectMocks
  private AIConfig aiConfig;

  private ChatMemory realChatMemory;

  @BeforeEach
  void setUp() {
    InMemoryChatMemoryRepository repository = new InMemoryChatMemoryRepository();
    realChatMemory = MessageWindowChatMemory.builder()
        .chatMemoryRepository(repository)
        .maxMessages(20)
        .build();

    when(mockChatClientBuilder.defaultAdvisors(any(Advisor.class)))
        .thenReturn(mockChatClientBuilder);

    when(mockChatClientBuilder.build()).thenReturn(mockChatClient);
  }

  @Test
  @DisplayName("chatClient bean should call build() on the builder and return the result")
  void chatClient_shouldBuildAndReturnClient() {
    ChatClient result = aiConfig.chatClient(mockChatClientBuilder, realChatMemory);

    assertThat(result).isSameAs(mockChatClient);
    verify(mockChatClientBuilder, times(1)).defaultAdvisors(any(Advisor.class));
    verify(mockChatClientBuilder, times(1)).build();
    verifyNoMoreInteractions(mockChatClientBuilder);
  }
}
