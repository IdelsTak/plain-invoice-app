package com.plaininvoice.invoice.validation;

import java.util.*;

public record MissingValue(String path, String code, Map<String, String> context) implements ValidationError {
  public MissingValue {
    Objects.requireNonNull(path, "path cannot be null");
    Objects.requireNonNull(code, "code cannot be null");
    Objects.requireNonNull(context, "context cannot be null");
    path = path.trim();
    code = code.trim();
    if (path.isEmpty()) {
      throw new IllegalArgumentException("path cannot be blank");
    }
    if (code.isEmpty()) {
      throw new IllegalArgumentException("code cannot be blank");
    }
    context = Map.copyOf(context);
  }
}
