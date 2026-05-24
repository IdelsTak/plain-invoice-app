package com.plaininvoice.invoice.draft;

import com.plaininvoice.invoice.numbering.*;
import java.util.*;

public record InvoiceIdentity(InvoiceNumber number) {
  public InvoiceIdentity {
    Objects.requireNonNull(number, "invoice number cannot be null");
  }
}
