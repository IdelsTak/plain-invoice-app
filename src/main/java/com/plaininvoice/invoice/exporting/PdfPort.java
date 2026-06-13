package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.pagination.*;

public interface PdfPort {
  PdfFile pdf(PageDocument pages);
}
