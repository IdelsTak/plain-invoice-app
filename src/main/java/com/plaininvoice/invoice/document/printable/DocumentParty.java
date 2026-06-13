package com.plaininvoice.invoice.document.printable;

import java.util.*;

public record DocumentParty(String name, String taxId, String email) {
  public DocumentParty {
    Objects.requireNonNull(name, "party name cannot be null");
    Objects.requireNonNull(taxId, "party tax id cannot be null");
    Objects.requireNonNull(email, "party email cannot be null");
    name = name.trim();
    taxId = taxId.trim();
    email = email.trim();
    if (name.isEmpty()) {
      throw new IllegalArgumentException("party name cannot be blank");
    }
  }
}
