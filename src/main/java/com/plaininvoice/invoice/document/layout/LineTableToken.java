package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record LineTableToken(List<LineToken> lines) {
  public LineTableToken {
    Objects.requireNonNull(lines, "line tokens cannot be null");
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("line table cannot be empty");
    }
    lines = List.copyOf(lines);
  }
}
