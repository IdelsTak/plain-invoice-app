package com.plaininvoice.invoice.creation;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.numbering.*;
import java.util.*;

public final class CreateInvoice {
  private final InvoiceNumberUniqueness uniqueness;

  public CreateInvoice(InvoiceNumberUniqueness uniqueness) {
    this.uniqueness = Objects.requireNonNull(uniqueness, "invoice number uniqueness cannot be null");
  }

  public CreateInvoiceResult execute(CreateInvoiceRequest request) {
    Objects.requireNonNull(request, "create invoice request cannot be null");

    var spec = request.spec();
    var number = spec.identity().number();
    uniqueness.verify(number);

    var invoice = new Invoice(
      number.formatted(),
      spec.parties().seller(),
      spec.parties().buyer(),
      spec.schedule().issuedOn(),
      spec.schedule().paymentTerms(),
      spec.lines().lineItems(),
      new InvoiceState.Draft()
    );

    return new CreateInvoiceResult(invoice);
  }
}
