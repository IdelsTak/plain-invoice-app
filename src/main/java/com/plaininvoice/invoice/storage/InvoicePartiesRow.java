package com.plaininvoice.invoice.storage;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public record InvoicePartiesRow(Party seller, Party buyer) {
  public InvoicePartiesRow {
    Objects.requireNonNull(seller, "seller row cannot be null");
    Objects.requireNonNull(buyer, "buyer row cannot be null");
  }
}
