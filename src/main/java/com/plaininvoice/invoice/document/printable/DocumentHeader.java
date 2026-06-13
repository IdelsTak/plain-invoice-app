package com.plaininvoice.invoice.document.printable;

import java.time.*;
import java.util.*;

public record DocumentHeader(String number, LocalDate issuedOn, String state) {
  public DocumentHeader {
    Objects.requireNonNull(number, "document number cannot be null");
    Objects.requireNonNull(issuedOn, "issue date cannot be null");
    Objects.requireNonNull(state, "document state cannot be null");
    number = number.trim();
    state = state.trim();
    if (number.isEmpty()) {
      throw new IllegalArgumentException("document number cannot be blank");
    }
    if (state.isEmpty()) {
      throw new IllegalArgumentException("document state cannot be blank");
    }
  }
}
