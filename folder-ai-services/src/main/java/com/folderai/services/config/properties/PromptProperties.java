package com.folderai.services.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Maps to the 'prompt' namespace in application.yml. This version handle separate
 * system and user prompt paths.
 *
 * @param directory Configuration for folder directory prompts.
 */
@ConfigurationProperties(prefix = "prompt")
@Validated
public record PromptProperties(
    Directory directory
) {

  /**
   * Nested record for Directory-related properties.
   *
   * @param systemPromptPath The classpath location of the directory system prompt.
   * @param userPromptPath   The classpath location of the directory user prompt.
   */
  public record Directory(@NotBlank String systemPromptPath, @NotBlank String userPromptPath) {

  }


}

