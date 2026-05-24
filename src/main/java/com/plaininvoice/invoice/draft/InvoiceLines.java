package com.plaininvoice.invoice.draft;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public record InvoiceLines(List<LineItem> lineItems) {
  public InvoiceLines {
    Objects.requireNonNull(lineItems, "line items cannot be null");
    lineItems = List.copyOf(lineItems);
  }
}
