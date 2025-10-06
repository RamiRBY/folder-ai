package com.folderai.services.service.ai.impl;

import com.folderai.services.common.enumeration.AIProvider;
import com.folderai.services.service.ai.AIProviderService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Implementation of AIProviderService to manage different AI providers.
 */
@Service
public class AIProviderServiceImpl implements AIProviderService {

  private final ChatClient openAiChatClient;
  private final ChatClient anthropicChatClient;

  public AIProviderServiceImpl(
      @Qualifier("openAiChatClient") ChatClient openAiChatClient,
      @Qualifier("anthropicChatClient") ChatClient anthropicChatClient) {
    this.openAiChatClient = openAiChatClient;
    this.anthropicChatClient = anthropicChatClient;
  }

  @Override
  public ChatClient getChatClient(AIProvider provider) {
    return switch (provider) {
      case OPENAI -> openAiChatClient;
      case ANTHROPIC -> anthropicChatClient;
    };
  }

  @Override
  public ChatClient getDefaultChatClient() {
    return anthropicChatClient;
  }
}