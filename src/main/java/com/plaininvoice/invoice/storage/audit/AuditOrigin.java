package com.plaininvoice.invoice.storage.audit;

import java.util.*;

public record AuditOrigin(String actor, String source) {
  public AuditOrigin {
    Objects.requireNonNull(actor, "audit actor cannot be null");
    Objects.requireNonNull(source, "audit source cannot be null");
    actor = actor.trim();
    source = source.trim();
    if (actor.isEmpty()) {
      throw new IllegalArgumentException("audit actor cannot be blank");
    }
    if (source.isEmpty()) {
      throw new IllegalArgumentException("audit source cannot be blank");
    }
  }
}
