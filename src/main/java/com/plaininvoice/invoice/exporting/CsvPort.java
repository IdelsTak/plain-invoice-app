package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.printable.*;

public interface CsvPort {
  CsvFile csv(InvoiceDocument document);
}
