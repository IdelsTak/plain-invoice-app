package com.plaininvoice.invoice;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;

final class ArchitectureRulesTest {

  private static final Path MAIN_JAVA = Path.of("src", "main", "java");
  private static final Path INVOICE_ROOT = MAIN_JAVA.resolve(Path.of("com", "plaininvoice", "invoice"));
  private static final Set<String> INVOICE_SLICES = Set.of(
    "creation",
    "document",
    "draft",
    "editing",
    "exporting",
    "lifecycle",
    "listing",
    "loading",
    "numbering",
    "pricing",
    "settings",
    "storage",
    "totals",
    "validation"
  );
  private static final Set<String> CORE_SLICES = Set.of(
    "draft",
    "lifecycle",
    "numbering",
    "pricing",
    "settings",
    "totals",
    "validation"
  );
  private static final Set<String> APP_SLICES = Set.of(
    "creation",
    "editing",
    "listing",
    "loading",
    "totals"
  );
  private static final Pattern IMPORT = Pattern.compile("^import\\s+([^;]+);", Pattern.MULTILINE);
  private static final Pattern CONTRACT_TYPE = Pattern.compile(
    "\\bpublic\\s+(record|sealed\\s+interface)\\s+([A-Z][A-Za-z0-9_]*)\\b"
  );

  @Test
  void invoiceRootHasOnlyNamedSlices() throws Exception {
    assertThat(invoiceSlices(), is(INVOICE_SLICES));
  }

  @Test
  void invoiceRootHasNoProductionTypes() throws Exception {
    assertThat(invoiceRootTypes(), is(empty()));
  }

  @Test
  void edgeApisStayInEdgeSlices() throws Exception {
    assertThat(edgeApiViolations(), is(empty()));
  }

  @Test
  void coreSlicesAvoidEdgeAdapters() throws Exception {
    assertThat(coreAdapterViolations(), is(empty()));
  }

  @Test
  void appContractsAreRecordsOrSealed() throws Exception {
    assertThat(appContractViolations(), is(empty()));
  }

  private Set<String> invoiceSlices() throws Exception {
    try (var files = Files.list(INVOICE_ROOT)) {
      return files
        .filter(Files::isDirectory)
        .map(path -> path.getFileName().toString())
        .collect(Collectors.toCollection(TreeSet::new));
    }
  }

  private List<String> invoiceRootTypes() throws Exception {
    try (var files = Files.list(INVOICE_ROOT)) {
      return files
        .filter(Files::isRegularFile)
        .map(Path::getFileName)
        .map(Path::toString)
        .filter(name -> name.endsWith(".java"))
        .sorted()
        .toList();
    }
  }

  private List<String> edgeApiViolations() throws Exception {
    return sources()
      .flatMap(source -> edgeApiViolations(source).stream())
      .sorted()
      .toList();
  }

  private List<String> edgeApiViolations(SourceFile source) {
    var found = new ArrayList<String>();
    var matcher = IMPORT.matcher(source.content());
    while (matcher.find()) {
      var imported = matcher.group(1);
      if (edgeApiMisplaced(source.packageName(), imported)) {
        found.add(source.path() + ":" + imported);
      }
    }
    return found;
  }

  private boolean edgeApiMisplaced(String packageName, String imported) {
    return javaFxMisplaced(packageName, imported)
      || pdfMisplaced(packageName, imported)
      || sqliteMisplaced(packageName, imported);
  }

  private boolean javaFxMisplaced(String packageName, String imported) {
    return (imported.startsWith("javafx.") || imported.equals("javafx.print.PrinterJob"))
      && !packageName.startsWith("com.plaininvoice.workbench");
  }

  private boolean pdfMisplaced(String packageName, String imported) {
    return imported.startsWith("org.apache.pdfbox.")
      && !packageName.equals("com.plaininvoice.invoice.exporting");
  }

  private boolean sqliteMisplaced(String packageName, String imported) {
    return (imported.startsWith("org.sqlite.") || imported.startsWith("java.sql.") || imported.startsWith("javax.sql."))
      && !packageName.startsWith("com.plaininvoice.invoice.storage");
  }

  private List<String> coreAdapterViolations() throws Exception {
    return sources()
      .filter(this::coreSource)
      .flatMap(source -> adapterViolations(source).stream())
      .sorted()
      .toList();
  }

  private boolean coreSource(SourceFile source) {
    return CORE_SLICES.stream()
      .map(slice -> "com.plaininvoice.invoice." + slice)
      .anyMatch(source.packageName()::startsWith);
  }

  private List<String> adapterViolations(SourceFile source) {
    var found = new ArrayList<String>();
    var matcher = IMPORT.matcher(source.content());
    while (matcher.find()) {
      var imported = matcher.group(1);
      if (edgeAdapterImport(imported)) {
        found.add(source.path() + ":" + imported);
      }
    }
    return found;
  }

  private boolean edgeAdapterImport(String imported) {
    return imported.startsWith("com.plaininvoice.invoice.storage.")
      || imported.startsWith("com.plaininvoice.invoice.exporting.")
      || imported.startsWith("org.apache.pdfbox.")
      || imported.startsWith("org.sqlite.")
      || imported.startsWith("javafx.");
  }

  private List<String> appContractViolations() throws Exception {
    return sources()
      .filter(this::appSource)
      .filter(this::contractFile)
      .filter(source -> !declaresContract(source.content()))
      .map(source -> source.path().toString())
      .sorted()
      .toList();
  }

  private boolean appSource(SourceFile source) {
    return APP_SLICES.stream()
      .map(slice -> "com.plaininvoice.invoice." + slice)
      .anyMatch(source.packageName()::startsWith);
  }

  private boolean contractFile(SourceFile source) {
    var name = source.path().getFileName().toString();
    return name.endsWith("Request.java")
      || name.endsWith("Result.java")
      || name.endsWith("Summary.java")
      || name.endsWith("Detail.java");
  }

  private boolean declaresContract(String source) {
    var matcher = CONTRACT_TYPE.matcher(source);
    return matcher.find();
  }

  private Stream<SourceFile> sources() throws Exception {
    if (!Files.exists(MAIN_JAVA)) {
      return Stream.empty();
    }
    return Files.walk(MAIN_JAVA)
      .filter(Files::isRegularFile)
      .filter(path -> path.getFileName().toString().endsWith(".java"))
      .map(this::source);
  }

  private SourceFile source(Path path) {
    try {
      var content = Files.readString(path);
      return new SourceFile(path, content, packageName(content));
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to read " + path, ex);
    }
  }

  private String packageName(String source) {
    var matcher = Pattern.compile("^package\\s+([^;]+);", Pattern.MULTILINE).matcher(source);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return "";
  }

  private record SourceFile(Path path, String content, String packageName) {
  }
}
