package com.plaininvoice.invoice.totals;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import java.util.*;

public final class LineTax {
  private final MonetaryArithmeticPolicy policy;

  public LineTax() {
    this(new MonetaryArithmeticPolicy());
  }

  public LineTax(MonetaryArithmeticPolicy policy) {
    this.policy = Objects.requireNonNull(policy, "monetary arithmetic policy cannot be null");
  }

  public Money total(Invoice invoice) {
    Objects.requireNonNull(invoice, "invoice cannot be null");
    var lines = invoice.lineItems();
    var seed = tax(lines.getFirst());
    for (var i = 1; i < lines.size(); i++) {
      seed = seed.add(tax(lines.get(i)));
    }
    return seed;
  }

  public Money tax(LineItem line) {
    Objects.requireNonNull(line, "line item cannot be null");
    var base = line.subtotal();
    var raw = base.amount().multiply(line.taxRate().factor());
    var value = policy.normalize(raw, base.currencyCode());
    return new Money(value, base.currencyCode());
  }
}
