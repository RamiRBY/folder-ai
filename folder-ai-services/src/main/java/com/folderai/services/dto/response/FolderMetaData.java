package com.folderai.services.dto.response;

/**
 * Nested record to structure metadata from the AI provider.
 *
 * @param model The model that generated the response.
 * @param usage Details on token consumption.
 */
public record FolderMetaData(
    String model,
    FolderUsage usage
) {

}

