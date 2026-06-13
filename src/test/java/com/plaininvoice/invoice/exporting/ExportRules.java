package com.plaininvoice.invoice.exporting;

import java.nio.charset.*;
import java.util.*;

record ExportRules(String format, String extension, Charset charset, String lineBreak, String comparison) {
  ExportRules {
    Objects.requireNonNull(format, "export format cannot be null");
    Objects.requireNonNull(extension, "export extension cannot be null");
    Objects.requireNonNull(charset, "export charset cannot be null");
    Objects.requireNonNull(lineBreak, "export line break cannot be null");
    Objects.requireNonNull(comparison, "export comparison cannot be null");
    format = format.trim();
    extension = extension.trim();
    comparison = comparison.trim();
    if (format.isEmpty()) {
      throw new IllegalArgumentException("export format cannot be blank");
    }
    if (extension.isEmpty()) {
      throw new IllegalArgumentException("export extension cannot be blank");
    }
    if (comparison.isEmpty()) {
      throw new IllegalArgumentException("export comparison cannot be blank");
    }
  }

  static ExportRules html() {
    return new ExportRules("html", ".html", StandardCharsets.UTF_8, "\n", "semantic text and stable markup");
  }

  static ExportRules pdfText() {
    return new ExportRules("pdf", ".pdf", StandardCharsets.UTF_8, "\n", "extracted text and explicit metadata");
  }

  static ExportRules csv() {
    return new ExportRules("csv", ".csv", StandardCharsets.UTF_8, "\r\n", "RFC 4180 records and headers");
  }
}
