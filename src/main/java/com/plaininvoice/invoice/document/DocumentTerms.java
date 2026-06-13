package com.plaininvoice.invoice.document;

import java.time.*;
import java.util.*;

public record DocumentTerms(LocalDate dueDate, String note) {
  public DocumentTerms {
    Objects.requireNonNull(dueDate, "payment due date cannot be null");
    Objects.requireNonNull(note, "payment note cannot be null");
    note = note.trim();
  }
}
