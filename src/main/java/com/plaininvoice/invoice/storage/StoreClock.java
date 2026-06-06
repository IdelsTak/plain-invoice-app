package com.plaininvoice.invoice.storage;

import java.time.*;
import java.util.*;

public record StoreClock(Instant createdAt, Instant updatedAt) {
  public StoreClock {
    Objects.requireNonNull(createdAt, "created timestamp cannot be null");
    Objects.requireNonNull(updatedAt, "updated timestamp cannot be null");
  }
}
