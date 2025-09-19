package com.folderai.services.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Architectural tests for the entire application. These rules enforce the overall structure, such
 * as layered architecture, dependency rules, and location of classes, to ensure the codebase
 * remains clean and maintainable.
 */
@AnalyzeClasses(
    packages = "com.folderai.services",
    importOptions = {ImportOption.DoNotIncludeTests.class})
public class ApplicationArchTest {

  /**
   * Defines and enforces the layered architecture of the application. - Web layer can access the
   * Service layer. - Service layer can access the Repository layer. - Dependencies must flow in one
   * direction (e.g., Services cannot access Web).
   */
  @ArchTest
  static final ArchRule layered_architecture_is_respected =
      layeredArchitecture()
          .consideringAllDependencies()
          // Layer Definitions
          .layer("Controller").definedBy("..controller..")
          .layer("Service").definedBy("..service..")

          // Dependency Rules
          .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
          .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
          .as("The layered architecture must be respected");


}

