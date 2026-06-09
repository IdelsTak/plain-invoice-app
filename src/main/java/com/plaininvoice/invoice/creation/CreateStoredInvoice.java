package com.plaininvoice.invoice.creation;

import com.plaininvoice.invoice.storage.*;
import java.util.*;

public final class CreateStoredInvoice {
  private final CreateInvoice create;
  private final InvoiceRepository invoices;

  public CreateStoredInvoice(CreateInvoice create, InvoiceRepository invoices) {
    this.create = Objects.requireNonNull(create, "create invoice use case cannot be null");
    this.invoices = Objects.requireNonNull(invoices, "invoice repository cannot be null");
  }

  public CreateStoredInvoiceResult execute(CreateStoredInvoiceRequest request) {
    Objects.requireNonNull(request, "create stored invoice request cannot be null");
    var created = create.execute(request.invoice()).invoice();
    var meta = new InvoiceStoreMeta(new InvoiceStoreKey(request.id(), 0), request.clock(), Optional.empty());
    try {
      return new CreateStoredInvoiceResult.Saved(invoices.save(new StoredInvoice(meta, created)));
    } catch (StoreConflict ex) {
      return new CreateStoredInvoiceResult.Conflict(request.id(), ex.getMessage());
    }
  }
}
