package com.plaininvoice.invoice.exporting;

import java.util.*;

record GoldenText(String expected) {
  GoldenText {
    expected = normalize(Objects.requireNonNull(expected, "expected golden text cannot be null"));
  }

  GoldenMatch compare(String actual) {
    var normalized = normalize(Objects.requireNonNull(actual, "actual golden text cannot be null"));
    return new GoldenMatch(expected.equals(normalized), expected, normalized);
  }

  private static String normalize(String value) {
    return value.replace("\r\n", "\n").replace('\r', '\n').stripTrailing();
  }
}
