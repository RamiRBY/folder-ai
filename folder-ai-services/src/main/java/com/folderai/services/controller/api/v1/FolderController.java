package com.folderai.services.controller.api.v1;

import com.folderai.services.dto.request.FolderRequest;
import com.folderai.services.dto.response.FolderResponse;
import com.folderai.services.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The main REST controller for the Scaffold AI application. This class handles all incoming API
 * requests related to project scaffolding.
 */
@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Folder AI", description = "Endpoints for generating project directory.")
public class FolderController {

  private final FolderService folderService;

  @Operation(summary = "Analyze prompt and return project directory",
      description = "Receives a chat prompt and returns a structured representation of folders to be generated.")
  @PostMapping("/project-structure")
  public ResponseEntity<FolderResponse> generateProjectStructure(
      @RequestBody FolderRequest folderRequest) {
    log.info("Received request to generate project structure, prompt : {}",
        folderRequest.prompt());
    return ResponseEntity.ok(folderService.generateProjectDirectory(folderRequest));
  }

}


