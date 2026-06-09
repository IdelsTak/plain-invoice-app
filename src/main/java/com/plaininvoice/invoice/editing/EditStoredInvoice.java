package com.plaininvoice.invoice.editing;

import com.plaininvoice.invoice.storage.*;
import java.util.*;

public final class EditStoredInvoice {
  private final EditInvoice edit;
  private final InvoiceRepository invoices;

  public EditStoredInvoice(EditInvoice edit, InvoiceRepository invoices) {
    this.edit = Objects.requireNonNull(edit, "edit invoice use case cannot be null");
    this.invoices = Objects.requireNonNull(invoices, "invoice repository cannot be null");
  }

  public EditStoredInvoiceResult execute(EditStoredInvoiceRequest request) {
    Objects.requireNonNull(request, "edit stored invoice request cannot be null");
    var current = request.current();
    var edited = edit.execute(new EditInvoiceRequest(current.invoice(), request.spec())).invoice();
    var meta = new InvoiceStoreMeta(
      current.meta().key(),
      new StoreClock(current.meta().clock().createdAt(), request.updatedAt()),
      current.meta().voidMark()
    );
    try {
      return new EditStoredInvoiceResult.Saved(invoices.save(new StoredInvoice(meta, edited)));
    } catch (StoreConflict ex) {
      return new EditStoredInvoiceResult.Conflict(current.meta().key().id(), ex.getMessage());
    }
  }
}
