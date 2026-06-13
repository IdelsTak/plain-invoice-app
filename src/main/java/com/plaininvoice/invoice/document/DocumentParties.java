package com.plaininvoice.invoice.document;

import java.util.*;

public record DocumentParties(DocumentParty seller, DocumentParty buyer) {
  public DocumentParties {
    Objects.requireNonNull(seller, "seller cannot be null");
    Objects.requireNonNull(buyer, "buyer cannot be null");
  }
}
