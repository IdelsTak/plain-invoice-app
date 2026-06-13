package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record LayoutPage(String size, String orientation, PageMargins margins) {
  public LayoutPage {
    Objects.requireNonNull(size, "page size cannot be null");
    Objects.requireNonNull(orientation, "page orientation cannot be null");
    Objects.requireNonNull(margins, "page margins cannot be null");
    size = size.trim();
    orientation = orientation.trim();
    if (size.isEmpty()) {
      throw new IllegalArgumentException("page size cannot be blank");
    }
    if (orientation.isEmpty()) {
      throw new IllegalArgumentException("page orientation cannot be blank");
    }
  }
}
