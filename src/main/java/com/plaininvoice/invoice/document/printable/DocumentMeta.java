package com.plaininvoice.invoice.document.printable;

import java.util.*;

public record DocumentMeta(String title, String language) {
  public DocumentMeta {
    Objects.requireNonNull(title, "document title cannot be null");
    Objects.requireNonNull(language, "document language cannot be null");
    title = title.trim();
    language = language.trim();
    if (title.isEmpty()) {
      throw new IllegalArgumentException("document title cannot be blank");
    }
    if (language.isEmpty()) {
      throw new IllegalArgumentException("document language cannot be blank");
    }
  }
}
