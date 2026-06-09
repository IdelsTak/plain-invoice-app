package com.plaininvoice.invoice.storage;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.time.*;
import org.junit.jupiter.api.*;

final class StoreBackupArchiveTest {

  @Test
  void rejectsNullPath() {
    assertThrows(NullPointerException.class, () -> new StoreBackupArchive(null, now(), "store.sqlite"));
  }

  @Test
  void rejectsNullTimestamp() {
    assertThrows(NullPointerException.class, () -> new StoreBackupArchive(Path.of("backup.zip"), null, "store.sqlite"));
  }

  @Test
  void rejectsNullName() {
    assertThrows(NullPointerException.class, () -> new StoreBackupArchive(Path.of("backup.zip"), now(), null));
  }

  @Test
  void rejectsBlankName() {
    assertThrows(IllegalArgumentException.class, () -> new StoreBackupArchive(Path.of("backup.zip"), now(), " "));
  }

  @Test
  void trimsName() {
    assertThat(new StoreBackupArchive(Path.of("backup.zip"), now(), " store.sqlite ").databaseName(), is("store.sqlite"));
  }

  private Instant now() {
    return Instant.parse("2026-05-24T10:15:30Z");
  }
}
