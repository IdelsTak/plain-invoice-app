package com.plaininvoice.invoice.storage.local;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import org.junit.jupiter.api.*;

final class StoreHomeTest {

  @Test
  void rejectsNullDirectory() {
    assertThrows(NullPointerException.class, () -> new StoreHome(null));
  }

  @Test
  void rejectsNullDatabaseName() {
    assertThrows(NullPointerException.class, () -> new StoreHome(Path.of("data"), null));
  }

  @Test
  void rejectsBlankDatabaseName() {
    assertThrows(IllegalArgumentException.class, () -> new StoreHome(Path.of("data"), " "));
  }

  @Test
  void rejectsDatabasePath() {
    assertThrows(IllegalArgumentException.class, () -> new StoreHome(Path.of("data"), "nested/store.sqlite"));
  }

  @Test
  void defaultsDatabaseName() {
    assertThat(new StoreHome(Path.of("data")).database(), is(Path.of("data", "plain-invoice.sqlite")));
  }

  @Test
  void trimsDatabaseName() {
    assertThat(new StoreHome(Path.of("data"), " invoices.sqlite ").database(), is(Path.of("data", "invoices.sqlite")));
  }
}
