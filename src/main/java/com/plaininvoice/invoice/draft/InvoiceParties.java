package com.plaininvoice.invoice.draft;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public record InvoiceParties(Party seller, Party buyer) {
  public InvoiceParties {
    Objects.requireNonNull(seller, "seller cannot be null");
    Objects.requireNonNull(buyer, "buyer cannot be null");
  }
}
