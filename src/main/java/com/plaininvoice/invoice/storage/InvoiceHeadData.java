package com.plaininvoice.invoice.storage;

import java.util.*;

public record InvoiceHeadData(InvoicePartiesRow parties, InvoiceScheduleRow schedule, InvoiceStateRow state) {
  public InvoiceHeadData {
    Objects.requireNonNull(parties, "invoice parties row cannot be null");
    Objects.requireNonNull(schedule, "invoice schedule row cannot be null");
    Objects.requireNonNull(state, "invoice state row cannot be null");
  }
}
