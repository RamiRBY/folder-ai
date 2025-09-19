package com.folderai.services.dto.response;

/**
 * Nested record for token usage details. Field names are mapped to common provider outputs using
 *
 * @param promptTokens     Tokens used in the prompt.
 * @param generationTokens Tokens used in the generated response.
 * @param totalTokens      Total tokens consumed for the API call.
 */
public record FolderUsage(
    Integer promptTokens,
    Integer generationTokens,
    Integer totalTokens
) {

}
