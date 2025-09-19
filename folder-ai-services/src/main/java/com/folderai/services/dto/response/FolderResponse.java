package com.folderai.services.dto.response;


import java.util.UUID;

/**
 * Chat response DTO. Includes the AI-generated content, conversation tracking ID, and detailed
 * metadata.
 *
 * @param directoryStructure           A nested map representing directory structure and project
 *                                     name.
 * @param conversationId conversationId The unique identifier for the conversation, echoed from the
 *                       request.
 * @param metaData       metadata Contains usage information (like token counts) and other metadata
 *                       from the provider.
 */

public record FolderResponse(
    DirectoryStructure directoryStructure,
    UUID conversationId,
    //or we can use spring MetaData, but it may cause an issue
    FolderMetaData metaData
) {

}
