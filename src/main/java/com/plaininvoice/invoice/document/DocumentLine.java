package com.plaininvoice.invoice.document;

import com.plaininvoice.invoice.pricing.*;
import java.util.*;

public record DocumentLine(
  int position,
  String description,
  Quantity quantity,
  Percentage taxRate,
  DocumentAmounts amounts
) {
  public DocumentLine {
    Objects.requireNonNull(description, "line description cannot be null");
    Objects.requireNonNull(quantity, "line quantity cannot be null");
    Objects.requireNonNull(taxRate, "line tax rate cannot be null");
    Objects.requireNonNull(amounts, "line amounts cannot be null");
    description = description.trim();
    if (position < 1) {
      throw new IllegalArgumentException("line position must be positive");
    }
    if (description.isEmpty()) {
      throw new IllegalArgumentException("line description cannot be blank");
    }
  }
}
