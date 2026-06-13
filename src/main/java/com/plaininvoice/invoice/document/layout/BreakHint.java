package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record BreakHint(String before, String inside, String after) {
  public BreakHint {
    Objects.requireNonNull(before, "break-before hint cannot be null");
    Objects.requireNonNull(inside, "break-inside hint cannot be null");
    Objects.requireNonNull(after, "break-after hint cannot be null");
    before = before.trim();
    inside = inside.trim();
    after = after.trim();
    if (before.isEmpty() || inside.isEmpty() || after.isEmpty()) {
      throw new IllegalArgumentException("break hints cannot be blank");
    }
  }
}
