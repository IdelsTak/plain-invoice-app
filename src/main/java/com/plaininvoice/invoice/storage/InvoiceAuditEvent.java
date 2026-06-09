package com.plaininvoice.invoice.storage;

import java.time.*;
import java.util.*;

public record InvoiceAuditEvent(String invoiceId, long version, Instant occurredAt, InvoiceAuditKind kind, String detail) {
  public InvoiceAuditEvent {
    Objects.requireNonNull(invoiceId, "audit invoice id cannot be null");
    Objects.requireNonNull(occurredAt, "audit timestamp cannot be null");
    Objects.requireNonNull(kind, "audit kind cannot be null");
    detail = detail == null ? "" : detail.trim();
    invoiceId = invoiceId.trim();
    if (invoiceId.isEmpty()) {
      throw new IllegalArgumentException("audit invoice id cannot be blank");
    }
    if (version < 0) {
      throw new IllegalArgumentException("audit invoice version cannot be negative");
    }
  }
}
