package com.plaininvoice.invoice.creation;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.numbering.*;
import com.plaininvoice.invoice.validation.*;
import java.util.*;

public final class CreateInvoice {
  private final InvoiceNumberUniqueness uniqueness;
  private final ValidationMap validationMap;

  public CreateInvoice(InvoiceNumberUniqueness uniqueness) {
    this.uniqueness = Objects.requireNonNull(uniqueness, "invoice number uniqueness cannot be null");
    this.validationMap = new ValidationMap("invoice", "invoice.create.invalid", Map.of("action", "create"));
  }

  public CreateInvoiceResult execute(CreateInvoiceRequest request) {
    Objects.requireNonNull(request, "create invoice request cannot be null");

    try {
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
    } catch (IllegalArgumentException ex) {
      throw new ValidationFault(List.of(validationMap.map(ex)), ex);
    }
  }
}
