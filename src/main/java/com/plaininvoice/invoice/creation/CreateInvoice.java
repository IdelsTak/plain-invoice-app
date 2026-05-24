package com.plaininvoice.invoice.creation;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public final class CreateInvoice {

  public CreateInvoiceResult execute(CreateInvoiceRequest request) {
    Objects.requireNonNull(request, "create invoice request cannot be null");

    var spec = request.spec();
    var invoice = new Invoice(
      spec.identity().number(),
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
