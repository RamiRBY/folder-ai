package com.folderai.services.config.ai;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring configuration class for AI-related beans.
 */
@Configuration
public class AIConfig {

  /**
   * Creates and configures a InMemoryChatMemoryRepository bean for the application.
   *
   * @return A configured InMemoryChatMemoryRepository bean ready for injection.
   */
  @Bean
  public InMemoryChatMemoryRepository inMemoryChatMemoryRepository() {
    return new InMemoryChatMemoryRepository();
  }

  /**
   * Creates and configures a ChatMemory bean for the application.
   *
   * @param repository the spring AI InMemoryChatMemoryRepository
   * @return A configured ChatMemory bean ready for injection.
   */
  @Bean
  public ChatMemory chatMemory(InMemoryChatMemoryRepository repository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(repository)
        .maxMessages(20)
        .build();
  }

  /**
   * Creates a ChatClient specifically for OpenAI.
   *
   * @param openAiChatModel The OpenAI ChatModel auto-configured by Spring AI
   * @param chatMemory The ChatMemory bean
   * @return A configured ChatClient for OpenAI
   */
  @Bean
  @Qualifier("openAiChatClient")
  public ChatClient openAiChatClient(
      OpenAiChatModel openAiChatModel,
      ChatMemory chatMemory) {
    return ChatClient.builder(openAiChatModel)
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build();
  }

  /**
   * Creates a ChatClient specifically for Anthropic Claude.
   *
   * @param anthropicChatModel The Anthropic ChatModel auto-configured by Spring AI
   * @param chatMemory The ChatMemory bean
   * @return A configured ChatClient for Anthropic Claude
   */
  @Bean
  @Qualifier("anthropicChatClient")
  public ChatClient anthropicChatClient(
      AnthropicChatModel anthropicChatModel,
      ChatMemory chatMemory) {
    return ChatClient.builder(anthropicChatModel)
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build();
  }

  /**
   * Creates the default/primary ChatClient bean for the application.
   * Uses OpenAI by default, but can be changed to use Anthropic.
   *
   * @param anthropicChatClient The Anthropic ChatClient
   * @return The primary ChatClient bean
   */
  @Bean
  @Primary
  public ChatClient chatClient(@Qualifier("anthropicChatClient") ChatClient anthropicChatClient) {
    return anthropicChatClient;
  }

}
