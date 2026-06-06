package com.plaininvoice.invoice.storage;

import com.plaininvoice.invoice.lifecycle.*;
import java.time.*;
import java.util.*;

public record InvoiceScheduleRow(LocalDate issuedOn, PaymentTerms paymentTerms) {
  public InvoiceScheduleRow {
    Objects.requireNonNull(issuedOn, "issue date row cannot be null");
    Objects.requireNonNull(paymentTerms, "payment terms row cannot be null");
  }
}
