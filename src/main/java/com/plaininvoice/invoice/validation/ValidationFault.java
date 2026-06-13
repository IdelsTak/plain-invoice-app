package com.plaininvoice.invoice.validation;

import java.util.*;

public final class ValidationFault extends IllegalArgumentException {
  private final List<ValidationError> errors;

  public ValidationFault(List<ValidationError> errors) {
    super("validation failed");
    this.errors = List.copyOf(Objects.requireNonNull(errors, "validation errors cannot be null"));
    if (this.errors.isEmpty()) {
      throw new IllegalArgumentException("validation errors cannot be empty");
    }
  }

  public ValidationFault(List<ValidationError> errors, RuntimeException cause) {
    super("validation failed", Objects.requireNonNull(cause, "cause cannot be null"));
    this.errors = List.copyOf(Objects.requireNonNull(errors, "validation errors cannot be null"));
    if (this.errors.isEmpty()) {
      throw new IllegalArgumentException("validation errors cannot be empty");
    }
  }

  public List<ValidationError> errors() {
    return List.copyOf(errors);
  }
}
