package com.plaininvoice.invoice.draft;

import com.plaininvoice.invoice.lifecycle.*;
import java.time.*;
import java.util.*;

public record InvoiceSchedule(LocalDate issuedOn, PaymentTerms paymentTerms) {
  public InvoiceSchedule {
    Objects.requireNonNull(issuedOn, "issue date cannot be null");
    Objects.requireNonNull(paymentTerms, "payment terms cannot be null");
  }
}
