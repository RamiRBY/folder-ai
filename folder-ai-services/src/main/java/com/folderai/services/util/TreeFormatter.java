package com.folderai.services.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TreeFormatter {

  /**
   * Cleans and formats the raw tree string from an AI response for display.
   *
   * @param rawTree The raw string from the ProjectDirectory.tree() method.
   * @return A formatted, multi-line string suitable for logging or UI display.
   */
  public static String cleanAndFormatTree(String rawTree) {
    if (rawTree == null || rawTree.isBlank()) {
      return "";
    }

    return Stream.of(rawTree.split("\n"))
        .map(line -> line.replaceAll("\\p{Z}", " "))
        .collect(Collectors.joining(System.lineSeparator()));
  }

}