package com.folderai.services.dto.request;

/**
 * Record for model-specific parameters.
 *
 * @param model       The specific model to use for the request (e.g., "gpt-4o").
 * @param temperature The creativity/randomness of the response, typically between 0.0 and 1.0.
 **/

public record FolderModelOptions(String model, Double temperature) {

}
