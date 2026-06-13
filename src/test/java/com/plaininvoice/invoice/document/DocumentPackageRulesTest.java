package com.plaininvoice.invoice.document;

import java.nio.file.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class DocumentPackageRulesTest {

  @Test
  void documentRootHasNoProductionTypes() throws Exception {
    var rootTypes = Files.list(root())
      .filter(Files::isRegularFile)
      .map(Path::getFileName)
      .map(Path::toString)
      .filter(name -> name.endsWith(".java"))
      .toList();

    assertThat(rootTypes, is(empty()));
  }

  @Test
  void documentPackageAvoidsEdgeApis() throws Exception {
    var violations = sources()
      .filter(this::usesEdgeApi)
      .toList();

    assertThat(violations, is(empty()));
  }

  private Path root() {
    return Path.of("src", "main", "java", "com", "plaininvoice", "invoice", "document");
  }

  private Stream<String> sources() throws Exception {
    return Files.walk(root())
      .filter(Files::isRegularFile)
      .map(this::source);
  }

  private boolean usesEdgeApi(String source) {
    return source.contains("javafx")
      || source.contains("storage")
      || source.contains("pdfbox")
      || source.contains("PrinterJob")
      || source.contains("javafx.print")
      || source.contains("html");
  }

  private String source(Path path) {
    try {
      return Files.readString(path);
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to read " + path, ex);
    }
  }
}
