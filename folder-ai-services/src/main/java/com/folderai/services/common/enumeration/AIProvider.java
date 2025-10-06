package com.folderai.services.common.enumeration;

import lombok.Getter;

/**
 * Enum representing supported AI providers.
 */
@Getter
public enum AIProvider {
  OPENAI("OpenAI GPT"),
  ANTHROPIC("Anthropic Claude");

  private final String displayName;

  AIProvider(String displayName) {
    this.displayName = displayName;
  }

}