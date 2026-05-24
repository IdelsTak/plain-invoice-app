package com.plaininvoice.invoice.lifecycle;

import com.plaininvoice.invoice.pricing.*;
import java.util.*;

public record LineItem(String description, Quantity quantity, Money unitPrice, Percentage taxRate) {
  public LineItem {
    Objects.requireNonNull(description, "line item description cannot be null");
    Objects.requireNonNull(quantity, "line item quantity cannot be null");
    Objects.requireNonNull(unitPrice, "line item unit price cannot be null");
    Objects.requireNonNull(taxRate, "line item tax rate cannot be null");
    description = description.trim();
    if (description.isEmpty()) {
      throw new IllegalArgumentException("line item description cannot be blank");
    }
  }

  public Money subtotal() {
    return unitPrice.multiply(quantity);
  }

  public TaxBreakdown tax() {
    return TaxBreakdown.from(subtotal(), taxRate);
  }

  public Money total() {
    return subtotal().add(tax().tax());
  }
}
