package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

public interface LayoutPort {
  LayoutDocument layout(InvoiceDocument document);
}
