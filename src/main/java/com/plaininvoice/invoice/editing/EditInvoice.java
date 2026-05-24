package com.plaininvoice.invoice.editing;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public final class EditInvoice {

  public EditInvoiceResult execute(EditInvoiceRequest request) {
    Objects.requireNonNull(request, "edit invoice request cannot be null");

    var current = request.current();
    if (!(current.state() instanceof InvoiceState.Draft)) {
      throw new IllegalStateException("only draft invoices can be edited");
    }

    var spec = request.spec();
    var edited = new Invoice(
      spec.identity().number(),
      spec.parties().seller(),
      spec.parties().buyer(),
      spec.schedule().issuedOn(),
      spec.schedule().paymentTerms(),
      spec.lines().lineItems(),
      current.state()
    );

    return new EditInvoiceResult(edited);
  }
}
