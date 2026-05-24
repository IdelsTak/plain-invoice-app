package com.plaininvoice.invoice.draft;

import java.util.*;

public record InvoiceDraftSpec(
  InvoiceIdentity identity,
  InvoiceParties parties,
  InvoiceSchedule schedule,
  InvoiceLines lines
) {
  public InvoiceDraftSpec {
    Objects.requireNonNull(identity, "invoice identity cannot be null");
    Objects.requireNonNull(parties, "invoice parties cannot be null");
    Objects.requireNonNull(schedule, "invoice schedule cannot be null");
    Objects.requireNonNull(lines, "invoice lines cannot be null");
  }
}
