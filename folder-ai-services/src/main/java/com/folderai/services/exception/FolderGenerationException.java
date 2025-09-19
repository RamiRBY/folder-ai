package com.folderai.services.exception;


/**
 * A custom exception for errors that occur during the folder generation process.
 */
public class FolderGenerationException extends RuntimeException {

  public FolderGenerationException(String message, Throwable cause){
    super(message,cause);
  }

}

