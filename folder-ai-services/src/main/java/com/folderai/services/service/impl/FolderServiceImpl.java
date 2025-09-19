package com.folderai.services.service.impl;

import com.folderai.services.ai.prompt.PromptFactory;
import com.folderai.services.dto.request.FolderRequest;
import com.folderai.services.dto.response.DirectoryStructure;
import com.folderai.services.dto.response.FolderMetaData;
import com.folderai.services.dto.response.FolderResponse;
import com.folderai.services.dto.response.FolderUsage;
import com.folderai.services.exception.FolderGenerationException;
import com.folderai.services.service.FolderService;
import com.folderai.services.util.TreeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the FolderService interface. This class contains the core logic for
 * interacting with the AI model to generate project structures.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FolderServiceImpl implements FolderService {

  private final ChatClient chatClient;
  private final ChatMemory chatMemory;
  private final PromptFactory promptFactory;


  @Override
  public FolderResponse generateProjectDirectory(FolderRequest folderRequest) {
    {
      final var conversationId =
          (folderRequest.conversationId() == null || folderRequest.conversationId().toString()
              .isBlank())
              ? UUID.randomUUID().toString()
              : folderRequest.conversationId().toString();
      try {

        var converter = new BeanOutputConverter<>(DirectoryStructure.class);
        var prompt = promptFactory.createStructurePrompt(folderRequest.prompt(),
            converter.getFormat());
        log.info("Project directory: Calling AI model for conversationId: {}", conversationId);
        return callAiModel(prompt, getChatOptions(folderRequest), conversationId, converter);
      } catch (Exception e) {
        log.error("Error during folder generation for conversationId: {}.",
            conversationId);

        if (e instanceof NonTransientAiException) {
          throw new FolderGenerationException("Current quota was exceeded.", e);

        }
        log.error("Error during folder generation for conversationId: {}.",
            conversationId);
        throw new FolderGenerationException("Error during folder generation", e);
      }

    }

  }

  private ChatOptions getChatOptions(FolderRequest folderRequest) {
    var chatOptionsBuilder = ChatOptions.builder();
    if (folderRequest.chatOptions() != null) {
      if (folderRequest.chatOptions().model() != null && !folderRequest.chatOptions().model()
          .isBlank()) {
        chatOptionsBuilder.model(folderRequest.chatOptions().model());
      }
      if (folderRequest.chatOptions().temperature() != null) {
        chatOptionsBuilder.temperature(folderRequest.chatOptions().temperature());
      }
    }
    return chatOptionsBuilder.build();
  }

  private FolderResponse callAiModel(Prompt prompt, ChatOptions chatOptions, String conversationId,
      BeanOutputConverter<DirectoryStructure> converter
  ) {
    chatMemory.add(conversationId, prompt.getUserMessage());
    var rawResponse = chatClient.prompt(prompt)
        .options(chatOptions)
        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId)).call()
        .chatResponse();
    var rawTextContent = Optional.of(Objects.requireNonNull(rawResponse))
        .map(org.springframework.ai.chat.model.ChatResponse::getResult)
        .map(Generation::getOutput)
        .map(AbstractMessage::getText)
        .filter(text -> !text.isBlank()) // Also check that the text isn't empty
        .orElseThrow(() -> new FolderGenerationException("AI response was empty or malformed"
            + ".", new NullPointerException()));

    var parsedData = converter.convert(rawTextContent);
    if (parsedData != null) {
      log.info("Formatted Project Structure:\n{}",
          TreeFormatter.cleanAndFormatTree(parsedData.tree()));
    } else {
      throw new FolderGenerationException("AI response was empty", new Exception());
    }
    chatMemory.add(conversationId, new AssistantMessage(parsedData.tree()));
    return new FolderResponse(
        parsedData,
        UUID.fromString(conversationId),
        new FolderMetaData(
            rawResponse.getMetadata().getModel(),
            new FolderUsage(
                rawResponse.getMetadata().getUsage().getPromptTokens(),
                rawResponse.getMetadata().getUsage().getCompletionTokens(),
                rawResponse.getMetadata().getUsage().getTotalTokens()
            )
        )
    );
  }

}
