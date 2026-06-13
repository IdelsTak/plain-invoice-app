package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record PartyToken(String role, DocumentParty party) {
  public PartyToken {
    Objects.requireNonNull(role, "party role cannot be null");
    Objects.requireNonNull(party, "party cannot be null");
    role = role.trim();
    if (role.isEmpty()) {
      throw new IllegalArgumentException("party role cannot be blank");
    }
  }
}
