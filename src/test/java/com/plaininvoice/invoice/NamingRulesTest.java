package com.plaininvoice.invoice;

import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;

final class NamingRulesTest {

  private static final Path MAIN_JAVA = Path.of("src", "main", "java");
  private static final int MAX_CLASS_NAME_LENGTH = 30;
  private static final int MAX_METHOD_NAME_LENGTH = 18;
  private static final Set<String> BANNED_PACKAGES = Set.of("util", "manager", "service", "model", "dao");
  private static final Set<String> BANNED_ROLE_SUFFIXES = Set.of(
    "Parser",
    "Manager",
    "Service",
    "Model",
    "Dao",
    "Helper",
    "Processor",
    "Factory"
  );
  private static final Pattern CLASS_NAME = Pattern.compile("\\b(?:class|record|interface)\\s+([A-Za-z][A-Za-z0-9_]*)");
  private static final Pattern METHOD_NAME = Pattern.compile(
    "\\b(?:public|protected|private)\\s+(?:static\\s+)?(?:[A-Za-z0-9_<>,\\[\\]?]+\\s+)+([a-z][A-Za-z0-9_]*)\\s*\\("
  );

  @Test
  void productionClassesAvoidTechnicalRoleSuffixes() throws Exception {
    var violatingTypes = javaFiles()
      .flatMap(path -> typeNames(path).stream())
      .filter(this::hasBannedRoleSuffix)
      .sorted()
      .toList();

    Assertions.assertTrue(
      violatingTypes.isEmpty(),
      () -> "Classes must not end with technical-role suffixes. Found: " + String.join(", ", violatingTypes)
    );
  }

  @Test
  void productionPackagesDontUseBannedTechnicalLayerNames() throws Exception {
    var violatingPackages = javaFiles()
      .map(this::packageSegments)
      .filter(segments -> segments.stream().anyMatch(BANNED_PACKAGES::contains))
      .map(segments -> String.join(".", segments))
      .sorted()
      .distinct()
      .toList();

    Assertions.assertTrue(
      violatingPackages.isEmpty(),
      () -> "Packages must not use util/manager/service/model/dao. Found: " + String.join(", ", violatingPackages)
    );
  }

  @Test
  void productionClassNamesStayShort() throws Exception {
    var violatingTypes = javaFiles()
      .flatMap(path -> typeNames(path).stream())
      .filter(name -> name.length() > MAX_CLASS_NAME_LENGTH)
      .sorted()
      .toList();

    Assertions.assertTrue(
      violatingTypes.isEmpty(),
      () -> "Class names must be at most " + MAX_CLASS_NAME_LENGTH + " characters. Found: " + String.join(", ", violatingTypes)
    );
  }

  @Test
  void productionMethodNamesStayShort() throws Exception {
    var violatingMethods = javaFiles()
      .flatMap(path -> methodNames(path).stream())
      .filter(name -> name.length() > MAX_METHOD_NAME_LENGTH)
      .sorted()
      .distinct()
      .toList();

    Assertions.assertTrue(
      violatingMethods.isEmpty(),
      () -> "Method names must be at most " + MAX_METHOD_NAME_LENGTH + " characters. Found: " + String.join(", ", violatingMethods)
    );
  }

  private Stream<Path> javaFiles() throws Exception {
    if (!Files.exists(MAIN_JAVA)) {
      return Stream.empty();
    }
    return Files.walk(MAIN_JAVA)
      .filter(Files::isRegularFile)
      .filter(path -> path.getFileName().toString().endsWith(".java"));
  }

  private List<String> typeNames(Path path) {
    try {
      var source = Files.readString(path);
      var names = new ArrayList<String>();
      var matcher = CLASS_NAME.matcher(source);
      while (matcher.find()) {
        names.add(matcher.group(1));
      }
      return names;
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to read " + path, ex);
    }
  }

  private List<String> methodNames(Path path) {
    try {
      var source = Files.readString(path);
      var names = new ArrayList<String>();
      var matcher = METHOD_NAME.matcher(source);
      while (matcher.find()) {
        names.add(matcher.group(1));
      }
      return names;
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to read " + path, ex);
    }
  }

  private List<String> packageSegments(Path path) {
    var relative = MAIN_JAVA.relativize(path).getParent();
    if (relative == null) {
      return List.of();
    }
    return StreamSupport.stream(relative.spliterator(), false)
      .map(Path::toString)
      .toList();
  }

  private boolean hasBannedRoleSuffix(String typeName) {
    return BANNED_ROLE_SUFFIXES.stream().anyMatch(typeName::endsWith);
  }
}
