package com.plaininvoice.invoice.document.printable;

public interface DocumentPort {
  InvoiceDocument document(DocumentRequest request);
}
