package com.plaininvoice.invoice.editing;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.numbering.*;
import java.util.*;

public final class EditInvoice {
  private final InvoiceNumberUniqueness uniqueness;
  private final InvoiceNumberScan parser;

  public EditInvoice(InvoiceNumberUniqueness uniqueness, InvoiceNumberScan parser) {
    this.uniqueness = Objects.requireNonNull(uniqueness, "invoice number uniqueness cannot be null");
    this.parser = Objects.requireNonNull(parser, "invoice number parser cannot be null");
  }

  public EditInvoiceResult execute(EditInvoiceRequest request) {
    Objects.requireNonNull(request, "edit invoice request cannot be null");

    var current = request.current();
    if (!(current.state() instanceof InvoiceState.Draft)) {
      throw new IllegalStateException("only draft invoices can be edited");
    }

    var spec = request.spec();
    var target = spec.identity().number();
    var existing = parser.parse(current.number());
    if (!target.equals(existing)) {
      uniqueness.verify(target);
    }

    var edited = new Invoice(
      target.formatted(),
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
