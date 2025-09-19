package com.folderai.services.exception;


/**
 * Custom exception to be thrown when a required template or folder file cannot be found.
 */

public class FolderFileNotFoundException extends RuntimeException {

  public FolderFileNotFoundException(String message) {
    super(message);
  }

  public FolderFileNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

