package com.plaininvoice.invoice.document;

public interface DocumentPort {
  InvoiceDocument document(DocumentRequest request);
}
