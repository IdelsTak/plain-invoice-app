package com.plaininvoice.invoice.storage.audit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class AuditOriginTest {

  @Test
  void rejectsBlankActor() {
    assertThrows(IllegalArgumentException.class, () -> new AuditOrigin(" ", "sqlite-repo"));
  }

  @Test
  void rejectsBlankSource() {
    assertThrows(IllegalArgumentException.class, () -> new AuditOrigin("system", " "));
  }
}
