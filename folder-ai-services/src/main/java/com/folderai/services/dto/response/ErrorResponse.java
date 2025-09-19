package com.folderai.services.dto.response;

import java.time.LocalDateTime;

/**
 * A DTO record for sending standardized error responses to the client.
 *
 * @param timestamp
 * @param status
 * @param error
 * @param message
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message

) {

}
