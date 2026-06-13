package com.plaininvoice.invoice.exporting;

import java.nio.charset.*;
import java.util.*;

public record CsvFile(String value, Charset charset) {
  public CsvFile {
    Objects.requireNonNull(value, "CSV value cannot be null");
    Objects.requireNonNull(charset, "CSV charset cannot be null");
    if (value.isEmpty()) {
      throw new IllegalArgumentException("CSV value cannot be empty");
    }
  }
}
