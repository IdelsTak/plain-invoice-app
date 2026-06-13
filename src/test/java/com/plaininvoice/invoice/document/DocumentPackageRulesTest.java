package com.plaininvoice.invoice.document;

import java.nio.file.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class DocumentPackageRulesTest {

  @Test
  void documentPackageAvoidsEdgeApis() throws Exception {
    var violations = sources()
      .filter(source -> source.contains("javafx") || source.contains("storage") || source.contains("pdf") || source.contains("print"))
      .toList();

    assertThat(violations, is(empty()));
  }

  private Stream<String> sources() throws Exception {
    return Files.walk(Path.of("src", "main", "java", "com", "plaininvoice", "invoice", "document"))
      .filter(Files::isRegularFile)
      .map(this::source);
  }

  private String source(Path path) {
    try {
      return Files.readString(path);
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to read " + path, ex);
    }
  }
}
