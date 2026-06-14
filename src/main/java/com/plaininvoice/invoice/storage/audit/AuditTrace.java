package com.plaininvoice.invoice.storage.audit;

import java.util.*;

public record AuditTrace(String operationId, AuditOrigin origin) {
  public AuditTrace {
    Objects.requireNonNull(operationId, "audit operation id cannot be null");
    Objects.requireNonNull(origin, "audit origin cannot be null");
    operationId = operationId.trim();
    if (operationId.isEmpty()) {
      throw new IllegalArgumentException("audit operation id cannot be blank");
    }
  }
}
