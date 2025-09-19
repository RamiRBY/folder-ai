package com.folderai.services.config.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("PromptProperties Test")
class PromptPropertiesTest {

  @Test
  @DisplayName("shouldExposeValues")
  void record_shouldExposeValues() {
    PromptProperties.Directory directory =
        new PromptProperties.Directory("classpath:/directory-system.txt",
            "classpath:/directory-user.txt");

    PromptProperties props = new PromptProperties(directory);

    assertThat(props.directory().systemPromptPath()).isEqualTo("classpath:/directory-system.txt");
    assertThat(props.directory().userPromptPath()).isEqualTo("classpath:/directory-user.txt");
  }
}
