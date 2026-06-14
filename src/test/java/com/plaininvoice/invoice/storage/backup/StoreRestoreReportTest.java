package com.plaininvoice.invoice.storage.backup;

import java.nio.file.*;
import java.time.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class StoreRestoreReportTest {

  @Test
  void rejectsNullArchive() {
    assertThrows(NullPointerException.class, () ->
      new StoreRestoreReport(null, Path.of("store.sqlite"), now(), "store.sqlite", false));
  }

  @Test
  void rejectsBlankDatabaseName() {
    assertThrows(IllegalArgumentException.class, () ->
      new StoreRestoreReport(Path.of("backup.zip"), Path.of("store.sqlite"), now(), " ", false));
  }

  @Test
  void trimsDatabaseName() {
    assertThat(
      new StoreRestoreReport(Path.of("backup.zip"), Path.of("store.sqlite"), now(), " store.sqlite ", true).databaseName(),
      is("store.sqlite")
    );
  }

  private Instant now() {
    return Instant.parse("2026-05-24T10:15:30Z");
  }
}
