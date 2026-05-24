package com.plaininvoice.invoice.validation;

import java.util.*;

public record ValidationMap(String path, String code, Map<String, String> context) {
  public ValidationMap {
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

  public ValidationError map(RuntimeException cause) {
    Objects.requireNonNull(cause, "cause cannot be null");
    var payload = withReason(cause);
    return switch (cause) {
      case NullPointerException _ -> new MissingValue(path, code, payload);
      case IllegalStateException _ -> new RuleViolation(path, code, payload);
      default -> new InvalidValue(path, code, payload);
    };
  }

  private Map<String, String> withReason(RuntimeException cause) {
    var payload = new HashMap<>(context);
    payload.put("reason", Objects.toString(cause.getMessage(), "unknown"));
    return Map.copyOf(payload);
  }
}
