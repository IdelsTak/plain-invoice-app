package com.plaininvoice.invoice.settings;

import com.plaininvoice.invoice.lifecycle.*;
import java.time.*;
import java.util.*;

public record PaymentDefault(int dueDays, String note) {
  public PaymentDefault {
    if (dueDays <= 0) {
      throw new IllegalArgumentException("default payment due days must be positive");
    }
    note = note == null ? "" : note.trim();
  }

  public PaymentTerms terms(LocalDate issuedOn) {
    Objects.requireNonNull(issuedOn, "invoice issue date cannot be null");
    return new PaymentTerms(issuedOn.plusDays(dueDays), note);
  }
}
