package com.plaininvoice.invoice.settings;

import java.util.*;

public record SettingsRecord(SellerProfile seller, InvoiceDefaults defaults) {
  public SettingsRecord {
    Objects.requireNonNull(seller, "seller profile cannot be null");
    Objects.requireNonNull(defaults, "invoice defaults cannot be null");
  }
}
