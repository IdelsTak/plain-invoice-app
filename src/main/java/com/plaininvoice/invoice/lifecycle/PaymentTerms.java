package com.plaininvoice.invoice.lifecycle;

import java.time.*;
import java.util.*;

public record PaymentTerms(LocalDate dueDate, String note) {
  public PaymentTerms {
    Objects.requireNonNull(dueDate, "payment due date cannot be null");
    note = note == null ? "" : note.trim();
  }

  public boolean overdueOn(LocalDate referenceDate) {
    return referenceDate.isAfter(dueDate);
  }
}
