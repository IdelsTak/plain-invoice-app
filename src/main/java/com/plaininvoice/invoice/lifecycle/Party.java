package com.plaininvoice.invoice.lifecycle;

import java.util.*;

public record Party(String name, String taxId, String email) {
  public Party {
    Objects.requireNonNull(name, "party name cannot be null");
    name = name.trim();
    if (name.isEmpty()) {
      throw new IllegalArgumentException("party name cannot be blank");
    }
    taxId = taxId == null ? "" : taxId.trim();
    email = email == null ? "" : email.trim();
  }
}
