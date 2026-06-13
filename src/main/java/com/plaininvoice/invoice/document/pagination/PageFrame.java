package com.plaininvoice.invoice.document.pagination;

import com.plaininvoice.invoice.document.layout.*;
import java.util.*;

public record PageFrame(int number, HeaderToken header, PageBody body, FooterToken footer) {
  public PageFrame {
    Objects.requireNonNull(header, "page header cannot be null");
    Objects.requireNonNull(body, "page body cannot be null");
    Objects.requireNonNull(footer, "page footer cannot be null");
    if (number < 1) {
      throw new IllegalArgumentException("page number must be positive");
    }
  }
}
