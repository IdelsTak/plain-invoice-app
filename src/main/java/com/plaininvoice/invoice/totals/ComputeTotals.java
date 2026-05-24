package com.plaininvoice.invoice.totals;

import java.util.*;

public final class ComputeTotals {

  public ComputeTotalsResult execute(ComputeTotalsRequest request) {
    Objects.requireNonNull(request, "compute totals request cannot be null");

    var invoice = request.invoice();
    return new ComputeTotalsResult(
      invoice.subtotal(),
      invoice.totalTax(),
      invoice.totalDue()
    );
  }
}
