package com.plaininvoice.invoice.storage.backup;

import com.plaininvoice.invoice.storage.local.*;
import java.nio.file.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class StoreRestoreRequestTest {

  @Test
  void rejectsNullHome() {
    assertThrows(NullPointerException.class, () -> new StoreRestoreRequest(null, Path.of("backup.zip"), false));
  }

  @Test
  void rejectsNullArchive() {
    assertThrows(NullPointerException.class, () -> new StoreRestoreRequest(new StoreHome(Path.of("store")), null, false));
  }

  @Test
  void recordsDryRun() {
    assertThat(new StoreRestoreRequest(new StoreHome(Path.of("store")), Path.of("backup.zip"), true).dryRun(), is(true));
  }
}
