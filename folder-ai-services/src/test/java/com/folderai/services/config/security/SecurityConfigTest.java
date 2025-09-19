package com.folderai.services.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the SecurityConfig class. This class uses @WebMvcTest to test the security filter
 * chain in isolation.
 */
@WebMvcTest
@Import({SecurityConfig.class, SecurityConfigTest.TestConfig.class})
class SecurityConfigTest {

  @Autowired
  private MockMvc mockMvc;

  /**
   * An explicit, static configuration class for the test. This ensures our TestController is
   * reliably added to the context as a bean.
   */
  @Configuration
  static class TestConfig {

    @Bean
    public TestController testController() {
      return new TestController();
    }
  }

  /**
   * A dummy controller to provide endpoints for the security tests.
   */
  @RestController
  static class TestController {

    @GetMapping({
        "/api/v1/folders/123",
        "/api/v1/templates/xyz"
    })
    public String permittedGetEndpoints() {
      return "OK";
    }

    @PostMapping("/api/v1/folders")
    public String permittedPostEndpoints() {
      return "OK";
    }

    @GetMapping("/api/v1/some-other-secured-endpoint")
    public String securedEndpoint() {
      return "This should not be accessible";
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "/api/v1/folders/123"
  })
  @DisplayName("should allow unauthenticated access to permitted GET endpoints")
  void shouldAllowAccessToPermittedGetEndpoints(String url) throws Exception {
    mockMvc.perform(get(url))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("should allow unauthenticated access to permitted POST endpoints")
  void shouldAllowAccessToPermittedPostEndpoints() throws Exception {
    mockMvc.perform(post("/api/v1/folders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("should deny unauthenticated access to secured endpoints")
  void shouldDenyAccessToSecuredEndpoints() throws Exception {
    mockMvc.perform(get("/api/v1/some-other-secured-endpoint"))
        .andExpect(status().isForbidden());
  }
}

