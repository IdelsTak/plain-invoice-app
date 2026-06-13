package com.plaininvoice.invoice.document.layout;

public record PageMargins(int top, int right, int bottom, int left) {
  public PageMargins {
    if (top < 0 || right < 0 || bottom < 0 || left < 0) {
      throw new IllegalArgumentException("page margins cannot be negative");
    }
  }
}
