package com.plaininvoice.invoice.totals;

import java.util.*;

public final class ComputeTotals {
  private final LineTax lineTax;

  public ComputeTotals() {
    this(new LineTax());
  }

  public ComputeTotals(LineTax lineTax) {
    this.lineTax = Objects.requireNonNull(lineTax, "line tax cannot be null");
  }

  public ComputeTotalsResult execute(ComputeTotalsRequest request) {
    Objects.requireNonNull(request, "compute totals request cannot be null");

    var invoice = request.invoice();
    var subtotal = invoice.subtotal();
    var totalTax = lineTax.total(invoice);
    return new ComputeTotalsResult(
      subtotal,
      totalTax,
      subtotal.add(totalTax)
    );
  }
}
