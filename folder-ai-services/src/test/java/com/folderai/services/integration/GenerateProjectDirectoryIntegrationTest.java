package com.folderai.services.integration;

import com.folderai.services.dto.request.FolderRequest;
import com.folderai.services.dto.response.DirectoryStructure;
import com.folderai.services.dto.response.FolderResponse;
import com.folderai.services.exception.FolderGenerationException;
import com.folderai.services.service.FolderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("GenerateProjectDirectoryIntegrationTest")
public class GenerateProjectDirectoryIntegrationTest {


  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FolderService folderService;

  @Test
  @DisplayName("should successfully generate a project when AI call is successful")
  void generateProjectDirectory_returnsStructure() throws Exception {
    // Prepare a fake GeneratedProject that the service would return
    var structureChatResponse = new FolderResponse(
        new DirectoryStructure("project name", "structure "), null,
        null);
    when(folderService.generateProjectDirectory(any(FolderRequest.class)))
        .thenReturn(structureChatResponse);

    // language=JSON
    String requestJson = """   
        {
              "prompt": "generate spring boot learn"
            }
        """;
    // Perform a real HTTP request against the controller
    mockMvc.perform(
            post("/api/v1/folders/project-directory") // <-- replace with your actual request
                // mapping
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("project name")));

  }

  @Test
  @DisplayName("should throw FolderGenerationException when AI call fails")
  void generateProjectDirectory_returns500WhenServiceThrows() throws Exception {
    // Arrange: Mock the service to throw our custom exception.
    String errorMessage = "Folder Generation Failed";
    when(folderService.generateProjectDirectory(any(FolderRequest.class)))
        .thenThrow(new FolderGenerationException(errorMessage, new Exception()));

    var requestJson = """   
        {
              "projectDescription": "My demo project",
              "structure": "{}"
            }
        """;

    // Act & Assert
    mockMvc.perform(
            post("/api/v1/folders/project-directory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.error").value("Folder Generation Failed"))
        .andExpect(jsonPath("$.message").value(errorMessage));
  }
}


