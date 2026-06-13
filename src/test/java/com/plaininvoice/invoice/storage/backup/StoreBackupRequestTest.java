package com.plaininvoice.invoice.storage.backup;

import com.plaininvoice.invoice.storage.local.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.time.*;
import org.junit.jupiter.api.*;

final class StoreBackupRequestTest {

  @Test
  void rejectsNullHome() {
    assertThrows(NullPointerException.class, () -> new StoreBackupRequest(null, Path.of("backups"), now()));
  }

  @Test
  void rejectsNullDirectory() {
    assertThrows(NullPointerException.class, () -> new StoreBackupRequest(home(), null, now()));
  }

  @Test
  void rejectsNullTimestamp() {
    assertThrows(NullPointerException.class, () -> new StoreBackupRequest(home(), Path.of("backups"), null));
  }

  @Test
  void keepsDirectory() {
    assertThat(new StoreBackupRequest(home(), Path.of("backups"), now()).directory(), is(Path.of("backups")));
  }

  private StoreHome home() {
    return new StoreHome(Path.of("data"));
  }

  private Instant now() {
    return Instant.parse("2026-05-24T10:15:30Z");
  }
}
