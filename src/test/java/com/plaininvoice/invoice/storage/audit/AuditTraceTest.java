package com.plaininvoice.invoice.storage.audit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class AuditTraceTest {

  @Test
  void rejectsBlankOperationId() {
    assertThrows(IllegalArgumentException.class, () -> new AuditTrace(" ", new AuditOrigin("system", "sqlite-repo")));
  }

  @Test
  void rejectsNullOrigin() {
    assertThrows(NullPointerException.class, () -> new AuditTrace("op-1", null));
  }
}
