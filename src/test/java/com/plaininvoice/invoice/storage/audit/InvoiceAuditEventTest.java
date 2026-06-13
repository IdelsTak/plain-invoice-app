package com.plaininvoice.invoice.storage.audit;

import java.time.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class InvoiceAuditEventTest {

  @Test
  void toleratesNullDetail() {
    var event = event("inv-1", 1, null);
    assertThat(event.detail(), is(""));
  }

  @Test
  void trimsDetail() {
    var event = event("inv-1", 1, " created ");
    assertThat(event.detail(), is("created"));
  }

  @Test
  void rejectsBlankId() {
    assertThrows(IllegalArgumentException.class, () -> event(" ", 1, "created"));
  }

  @Test
  void rejectsNegativeVersion() {
    assertThrows(IllegalArgumentException.class, () -> event("inv-1", -1, "created"));
  }

  private InvoiceAuditEvent event(String id, long version, String detail) {
    return new InvoiceAuditEvent(id, version, Instant.parse("2026-05-24T10:15:30Z"), new InvoiceAuditKind.Created(), detail);
  }
}
