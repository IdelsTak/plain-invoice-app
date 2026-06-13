package com.plaininvoice.invoice.exporting;

import java.util.*;

record GoldenMatch(boolean matched, String expected, String actual) {
  GoldenMatch {
    Objects.requireNonNull(expected, "expected golden text cannot be null");
    Objects.requireNonNull(actual, "actual golden text cannot be null");
  }
}
