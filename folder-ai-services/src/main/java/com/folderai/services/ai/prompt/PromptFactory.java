package com.folderai.services.ai.prompt;

import com.folderai.services.config.properties.PromptProperties;
import com.folderai.services.exception.FolderFileNotFoundException;
import com.folderai.services.exception.PromptGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

/**
 * A factory class responsible for creating structured prompts for the AI model by loading and
 * rendering externalized templates.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PromptFactory {

  private final PromptProperties promptProperties;
  private final ResourceLoader resourceLoader;

  /**
   * Creates a complete Prompt object for the directory generation.
   *
   * @param userPrompt         The user's specific request string.
   * @param formatInstructions The format instructions from the BeanOutputConverter.
   * @return A Prompt object containing formatted system and user messages.
   */
  public Prompt createStructurePrompt(String userPrompt, String formatInstructions) {
    return createPrompt(
        promptProperties.directory().systemPromptPath(),
        promptProperties.directory().userPromptPath(), Map.of("prompt", userPrompt),
        Map.of("format", formatInstructions)
    );
  }

  /**
   * Internal helper method to construct a Prompt, eliminating code duplication. It loads, renders,
   * and assembles the system and user messages.
   *
   * @param userMap          The user's input.
   * @param systemMap        The output format instructions.
   * @param systemPromptPath The classpath location of the system prompt template.
   * @param userPromptPath   The classpath location of the user prompt template.
   * @return A fully constructed Prompt object.
   */
  private Prompt createPrompt(String systemPromptPath, String userPromptPath,
      Map<String, Object> userMap, Map<String,
          Object> systemMap) {
    try {

      PromptTemplate userPromptTemplate = new PromptTemplate(loadPromptTemplate(userPromptPath));
      PromptTemplate systemPromptTemplate = new PromptTemplate(
          loadPromptTemplate(systemPromptPath));

      var systemMessage = systemPromptTemplate.createMessage(systemMap);
      var userMessage = userPromptTemplate.createMessage(userMap);

      return new Prompt(List.of(systemMessage, userMessage));
    } catch (Exception e) {
      log.error("Failed to create prompt from paths: {} and {}", systemPromptPath, userPromptPath,
          e);
      throw new PromptGenerationException("Failed to generate a complete prompt", e);
    }
  }

  /**
   * @param location location of the prompt file
   * @return A Resource Object corresponding to the prompt file
   */
  private Resource loadPromptTemplate(String location) throws UncheckedIOException  {
    try {
      var resource = resourceLoader.getResource(location);
      if (!resource.exists()) {
        log.error("Resource not found at location: {}", location);
        throw new IOException("Resource not found at location: " + location);
      }
      return resource;
    } catch (IOException e) {
      log.error("Failed to load prompt template from: {}", location, e);
      throw new FolderFileNotFoundException("Failed to load prompt template from: " + location, e);
    }
  }

}
