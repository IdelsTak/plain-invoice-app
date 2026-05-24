package com.plaininvoice.invoice.lifecycle;

import com.plaininvoice.invoice.pricing.*;
import java.util.*;

public record TaxBreakdown(Percentage rate, Money base, Money tax) {
  public TaxBreakdown {
    Objects.requireNonNull(rate, "tax rate cannot be null");
    Objects.requireNonNull(base, "tax base cannot be null");
    Objects.requireNonNull(tax, "tax amount cannot be null");
    Money expected = base.percent(rate);
    if (!expected.equals(tax)) {
      throw new IllegalArgumentException("tax amount does not match base and rate");
    }
  }

  public static TaxBreakdown from(Money base, Percentage rate) {
    return new TaxBreakdown(rate, base, base.percent(rate));
  }
}
