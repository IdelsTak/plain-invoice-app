package com.plaininvoice.invoice.storage;

import java.time.*;
import java.util.*;

public record VoidMark(Instant voidedAt, String reason) {
  public VoidMark {
    Objects.requireNonNull(voidedAt, "void timestamp cannot be null");
    reason = reason == null ? "" : reason.trim();
  }
}
