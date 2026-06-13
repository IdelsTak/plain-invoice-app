package com.plaininvoice.invoice.exporting;

import java.nio.charset.*;
import java.util.*;

public record HtmlPage(String value, Charset charset) {
  public HtmlPage {
    Objects.requireNonNull(value, "HTML value cannot be null");
    Objects.requireNonNull(charset, "HTML charset cannot be null");
    value = value.stripTrailing();
    if (value.isEmpty()) {
      throw new IllegalArgumentException("HTML value cannot be blank");
    }
  }
}
