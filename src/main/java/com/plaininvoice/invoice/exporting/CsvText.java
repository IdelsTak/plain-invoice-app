package com.plaininvoice.invoice.exporting;

import java.util.*;

final class CsvText {

  private CsvText() {}

  static String field(String value) {
    Objects.requireNonNull(value, "CSV field cannot be null");
    var escaped = value.replace("\"", "\"\"");
    if (quoted(escaped)) {
      return "\"" + escaped + "\"";
    }
    return escaped;
  }

  private static boolean quoted(String value) {
    return value.contains(",")
      || value.contains("\"")
      || value.contains("\n")
      || value.contains("\r")
      || value.startsWith(" ")
      || value.endsWith(" ");
  }
}
