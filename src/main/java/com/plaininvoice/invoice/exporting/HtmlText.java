package com.plaininvoice.invoice.exporting;

import java.util.*;

final class HtmlText {

  private HtmlText() {}

  static String escaped(String value) {
    Objects.requireNonNull(value, "HTML text cannot be null");
    return value
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#39;");
  }
}
