package com.plaininvoice.invoice.numbering;

public interface InvoiceNumberUniqueness {
  void verify(InvoiceNumber number);
}
