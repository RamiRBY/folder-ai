package com.folderai.services.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Architectural tests for the web/controller layer.
 * These rules enforce conventions and best practices for our REST controllers to ensure
 * consistency, maintainability, and a clean architecture.
 */
@AnalyzeClasses(
    packages = "com.folderai.services",
    importOptions = { ImportOption.DoNotIncludeTests.class } // 👈 added
)public class ControllerArchTest {

  @ArchTest
  static final ArchRule controllers_must_reside_in_a_controller_package =
      classes().that().areAnnotatedWith(RestController.class)
          .should().resideInAPackage("..controller..")
          .as("Controllers should reside in a '..controller..' package");

  @ArchTest
  static final ArchRule controllers_should_have_RestController_annotation =
      classes().that().resideInAPackage("..controller..")
          .should().beAnnotatedWith(RestController.class)
          .as("Classes in the controller package should be annotated with @RestController");

  @ArchTest
  static final ArchRule controllers_should_be_named_Controller =
      classes().that().areAnnotatedWith(RestController.class)
          .should().haveSimpleNameEndingWith("Controller")
          .as("Controller classes should be named with a 'Controller' suffix");

  @ArchTest
  static final ArchRule controllers_should_have_class_level_RequestMapping =
      classes().that().areAnnotatedWith(RestController.class)
          .should().beAnnotatedWith(RequestMapping.class)
          .as("Controllers should have a class-level @RequestMapping annotation to define a base path");

  @ArchTest
  static final ArchRule public_methods_in_controllers_should_return_ResponseEntity =
      methods().that().arePublic()
          .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
          // We can exclude methods like main, or specific framework methods if needed
          .should().haveRawReturnType(ResponseEntity.class)
          .as("Public methods in controllers should return ResponseEntity for consistent response handling");

  @ArchTest
  static final ArchRule controller_methods_should_not_be_native_private =
      noMethods().that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
          .should().bePrivate()
          .as("Private helper methods in controllers can obscure logic; prefer extracting to a service or helper class");

  @ArchTest
  static final ArchRule controllers_should_use_constructor_injection =
      noFields().that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
          .should().beAnnotatedWith(Autowired.class)
          .as("Controllers should use constructor injection instead of field injection");

}
