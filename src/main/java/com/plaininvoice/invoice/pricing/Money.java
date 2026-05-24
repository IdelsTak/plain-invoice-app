package com.plaininvoice.invoice.pricing;

import java.math.*;
import java.util.*;

public record Money(BigDecimal amount, CurrencyCode currencyCode) {
  public Money {
    Objects.requireNonNull(amount, "amount cannot be null");
    Objects.requireNonNull(currencyCode, "currency code cannot be null");
    amount = MonetaryArithmeticPolicy.DEFAULT.normalize(amount, currencyCode);
  }

  public Money add(Money other) {
    ensureSameCurrency(other);
    return new Money(amount.add(other.amount), currencyCode);
  }

  public Money subtract(Money other) {
    ensureSameCurrency(other);
    return new Money(amount.subtract(other.amount), currencyCode);
  }

  public Money multiply(Quantity quantity) {
    return new Money(amount.multiply(quantity.value()), currencyCode);
  }

  public Money percent(Percentage percentage) {
    return new Money(amount.multiply(percentage.factor()), currencyCode);
  }

  private void ensureSameCurrency(Money other) {
    if (!currencyCode.equals(other.currencyCode)) {
      throw new IllegalArgumentException("money currency mismatch");
    }
  }
}
