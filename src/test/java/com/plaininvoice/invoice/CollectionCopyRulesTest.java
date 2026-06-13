package com.plaininvoice.invoice;

import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;

final class CollectionCopyRulesTest {

  private static final Path MAIN_JAVA = Path.of("src", "main", "java");
  private static final Pattern RECORD_HEADER = Pattern.compile("\\brecord\\s+\\w+\\s*\\((.*?)\\)\\s*\\{", Pattern.DOTALL);
  private static final Pattern RECORD_COMPONENT = Pattern.compile("\\b(List|Set|Map)<[^>]+>\\s+(\\w+)\\b");
  private static final Pattern FIELD = Pattern.compile("\\bprivate\\s+final\\s+(List|Set|Map)<[^>]+>\\s+(\\w+)");
  private static final Pattern DIRECT_RETURN = Pattern.compile(
    "\\bpublic\\s+(List|Set|Map)<[^>]+>\\s+\\w+\\s*\\([^)]*\\)\\s*\\{\\s*return\\s+(\\w+)\\s*;\\s*\\}",
    Pattern.DOTALL
  );

  @Test
  void collectionFieldsAreDefensivelyCopied() throws Exception {
    var violations = javaFiles()
      .flatMap(path -> violations(path).stream())
      .sorted()
      .toList();

    Assertions.assertTrue(
      violations.isEmpty(),
      () -> "Collection fields/components must defensively copy inputs. Found: " + String.join(", ", violations)
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

  private List<String> violations(Path path) {
    try {
      var source = Files.readString(path);
      var found = new ArrayList<String>();
      found.addAll(recordViolations(path, source));
      found.addAll(violations(path, source, FIELD.matcher(source)));
      found.addAll(returnViolations(path, source));
      return found;
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to read " + path, ex);
    }
  }

  private List<String> returnViolations(Path path, String source) {
    var fields = fields(source);
    var found = new ArrayList<String>();
    var matcher = DIRECT_RETURN.matcher(source);
    while (matcher.find()) {
      var name = matcher.group(2);
      if (fields.contains(name)) {
        found.add(path + ":" + name + " returned directly");
      }
    }
    return found;
  }

  private Set<String> fields(String source) {
    var names = new HashSet<String>();
    var matcher = FIELD.matcher(source);
    while (matcher.find()) {
      names.add(matcher.group(2));
    }
    return names;
  }

  private List<String> recordViolations(Path path, String source) {
    var found = new ArrayList<String>();
    var headers = RECORD_HEADER.matcher(source);
    while (headers.find()) {
      found.addAll(violations(path, source, RECORD_COMPONENT.matcher(headers.group(1))));
    }
    return found;
  }

  private List<String> violations(Path path, String source, Matcher matcher) {
    var found = new ArrayList<String>();
    while (matcher.find()) {
      var type = matcher.group(1);
      var name = matcher.group(2);
      if (!copied(source, type, name)) {
        found.add(path + ":" + name);
      }
    }
    return found;
  }

  private boolean copied(String source, String type, String name) {
    return source.contains(type + ".copyOf(" + name + ")")
      || source.contains(type + ".copyOf(Objects.requireNonNull(" + name)
      || source.contains("new ArrayList<>(" + name + ")")
      || source.contains("new ArrayList<>(Objects.requireNonNull(" + name)
      || source.contains("new HashSet<>(" + name + ")")
      || source.contains("new HashSet<>(Objects.requireNonNull(" + name)
      || source.contains("new HashMap<>(" + name + ")");
  }
}
