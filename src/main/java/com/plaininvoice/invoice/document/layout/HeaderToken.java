package com.plaininvoice.invoice.document.layout;

import java.time.*;
import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record HeaderToken(String title, String number, LocalDate issuedOn, String state) {
  public HeaderToken {
    Objects.requireNonNull(title, "header title cannot be null");
    Objects.requireNonNull(number, "header number cannot be null");
    Objects.requireNonNull(issuedOn, "header issue date cannot be null");
    Objects.requireNonNull(state, "header state cannot be null");
    title = title.trim();
    number = number.trim();
    state = state.trim();
    if (title.isEmpty()) {
      throw new IllegalArgumentException("header title cannot be blank");
    }
    if (number.isEmpty()) {
      throw new IllegalArgumentException("header number cannot be blank");
    }
    if (state.isEmpty()) {
      throw new IllegalArgumentException("header state cannot be blank");
    }
  }
}
