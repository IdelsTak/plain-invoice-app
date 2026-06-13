package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record PageHints(String headerMode, String footerMode, BreakHint tableBreak) {
  public PageHints {
    Objects.requireNonNull(headerMode, "header mode cannot be null");
    Objects.requireNonNull(footerMode, "footer mode cannot be null");
    Objects.requireNonNull(tableBreak, "table break hint cannot be null");
    headerMode = headerMode.trim();
    footerMode = footerMode.trim();
    if (headerMode.isEmpty() || footerMode.isEmpty()) {
      throw new IllegalArgumentException("page hint modes cannot be blank");
    }
  }
}
