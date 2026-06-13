package com.plaininvoice.invoice.exporting;

import java.util.*;

public record PdfFile(byte[] bytes, String contentType) {
  public PdfFile {
    Objects.requireNonNull(bytes, "PDF bytes cannot be null");
    Objects.requireNonNull(contentType, "PDF content type cannot be null");
    if (bytes.length == 0) {
      throw new IllegalArgumentException("PDF bytes cannot be empty");
    }
    contentType = contentType.trim();
    if (contentType.isEmpty()) {
      throw new IllegalArgumentException("PDF content type cannot be blank");
    }
    bytes = bytes.clone();
  }

  @Override
  public byte[] bytes() {
    return bytes.clone();
  }
}
