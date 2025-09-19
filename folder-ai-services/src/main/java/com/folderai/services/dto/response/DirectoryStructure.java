package com.folderai.services.dto.response;

/**
 * A DTO record that defines the expected JSON structure for the AI's response. Using a record
 * provides an immutable, concise representation of this data structure.
 *
 * @param projectName The root name of the project, e.g., "java"
 * @param tree        A nested map representing the directory structure.
 */

public record DirectoryStructure(String projectName,
                                 String tree) {

}

