package com.plaininvoice.invoice.document.pagination;

public record PageRules(int linesPerPage) {
  public PageRules {
    if (linesPerPage < 1) {
      throw new IllegalArgumentException("lines per page must be positive");
    }
  }
}
