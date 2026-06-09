package com.plaininvoice.invoice.settings;

import com.plaininvoice.invoice.pricing.*;
import java.util.*;

public record TaxPreset(String label, Percentage rate) {
  public TaxPreset {
    Objects.requireNonNull(label, "tax preset label cannot be null");
    Objects.requireNonNull(rate, "tax preset rate cannot be null");
    label = label.trim();
    if (label.isEmpty()) {
      throw new IllegalArgumentException("tax preset label cannot be blank");
    }
  }
}
