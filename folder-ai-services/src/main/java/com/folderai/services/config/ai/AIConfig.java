package com.folderai.services.config.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
   * Creates and configures a ChatClient bean for the application. Spring AI provides a
   * pre-configured ChatClient.Builder that we can use to build the final ChatClient instance.
   *
   * @param builder    The ChatClient.Builder provided by the Spring AI auto-configuration.
   * @param chatMemory The ChatMemory bean.
   * @return A configured ChatClient bean ready for injection.
   */
  @Bean
  public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
    return builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
  }

}
