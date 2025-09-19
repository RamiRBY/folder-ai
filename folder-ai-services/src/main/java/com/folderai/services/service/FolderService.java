package com.folderai.services.service;

import com.folderai.services.dto.request.FolderRequest;
import com.folderai.services.dto.response.FolderResponse;

public interface FolderService {

  /**
   * Takes a user's request and orchestrates the process of generating a project structure in tree
   * format.
   *
   * @param folderRequest request
   * @return the project structure
   */
  FolderResponse generateProjectDirectory(FolderRequest folderRequest);

}
