package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.layout.*;
import com.plaininvoice.invoice.document.pagination.*;
import com.plaininvoice.invoice.document.printable.*;
import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

record ExportCase(String key, String purpose, Invoice invoice, DocumentMeta meta) {
  ExportCase {
    Objects.requireNonNull(key, "export case key cannot be null");
    Objects.requireNonNull(purpose, "export case purpose cannot be null");
    Objects.requireNonNull(invoice, "export case invoice cannot be null");
    Objects.requireNonNull(meta, "export case metadata cannot be null");
    key = key.trim();
    purpose = purpose.trim();
    if (key.isEmpty()) {
      throw new IllegalArgumentException("export case key cannot be blank");
    }
    if (purpose.isEmpty()) {
      throw new IllegalArgumentException("export case purpose cannot be blank");
    }
  }

  InvoiceDocument document() {
    return new BuildDocument().document(new DocumentRequest(invoice, meta));
  }

  LayoutDocument layout() {
    return new BuildLayout().layout(document());
  }

  PageDocument pages() {
    return new PaginateLayout().paginate(layout());
  }
}
