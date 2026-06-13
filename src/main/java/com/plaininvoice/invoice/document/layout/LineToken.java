package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.pricing.*;
import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record LineToken(
  int position,
  String description,
  Quantity quantity,
  Percentage taxRate,
  DocumentAmounts amounts,
  BreakHint breakHint
) {
  public LineToken {
    Objects.requireNonNull(description, "line token description cannot be null");
    Objects.requireNonNull(quantity, "line token quantity cannot be null");
    Objects.requireNonNull(taxRate, "line token tax rate cannot be null");
    Objects.requireNonNull(amounts, "line token amounts cannot be null");
    Objects.requireNonNull(breakHint, "line token break hint cannot be null");
    description = description.trim();
    if (position < 1) {
      throw new IllegalArgumentException("line token position must be positive");
    }
    if (description.isEmpty()) {
      throw new IllegalArgumentException("line token description cannot be blank");
    }
  }
}
