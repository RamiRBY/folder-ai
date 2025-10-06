package com.folderai.services.service.ai;
import com.folderai.services.common.enumeration.AIProvider;
import org.springframework.ai.chat.client.ChatClient;

/**
 * Service interface to manage and switch between different AI providers.
 */
public interface AIProviderService {

  /**
   * Get the appropriate ChatClient based on the aiProvider.
   *
   * @param provider The AI aiProvider to use
   * @return The corresponding ChatClient
   */
  ChatClient getChatClient(AIProvider provider);

  /**
   * Get the default ChatClient (OpenAI).
   *
   * @return The default ChatClient
   */
  ChatClient getDefaultChatClient();
}