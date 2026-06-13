package com.plaininvoice.invoice.document.layout;

import java.time.*;
import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record TermsToken(LocalDate dueDate, String note) {
  public TermsToken {
    Objects.requireNonNull(dueDate, "terms due date cannot be null");
    Objects.requireNonNull(note, "terms note cannot be null");
    note = note.trim();
  }
}
