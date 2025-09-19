package com.folderai.services.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for the folder generation request from the frontend. This is an immutable record that holds
 * project description.
 *
 * @param prompt         A description to help the AI generate more relevant code content.
 * @param conversationId An optional unique identifier to maintain conversational context.
 * @param chatOptions    Optional parameters to control the AI model's behavior.
 */
public record FolderRequest(
    @Schema(description = "A description to help the AI generate more relevant directory.",
        example = "A directory to learn Java development")
    @NotNull(message = "Prompt cannot be null.")
    String prompt,
    UUID conversationId,
    FolderModelOptions chatOptions
) {

}

