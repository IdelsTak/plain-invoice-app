package com.plaininvoice.invoice;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;

final class StoragePackageRulesTest {

  private static final Path STORAGE = Path.of("src", "main", "java", "com", "plaininvoice", "invoice", "storage");
  private static final Set<String> ROOT_TYPES = Set.of(
    "InvoiceRepository.java",
    "InvoiceStoreKey.java",
    "InvoiceStoreMeta.java",
    "StoreClock.java",
    "StoreConflict.java",
    "StoredInvoice.java",
    "VoidMark.java"
  );

  @Test
  void keepsRootStorageContractsOnly() throws Exception {
    assertThat(rootTypes(), is(ROOT_TYPES));
  }

  private Set<String> rootTypes() throws Exception {
    try (var files = Files.list(STORAGE)) {
      return files
        .filter(Files::isRegularFile)
        .map(path -> path.getFileName().toString())
        .collect(Collectors.toCollection(TreeSet::new));
    }
  }
}
