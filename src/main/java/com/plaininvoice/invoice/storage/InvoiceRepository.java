package com.plaininvoice.invoice.storage;

import java.util.*;

public interface InvoiceRepository {
  StoredInvoice save(StoredInvoice invoice);

  Optional<StoredInvoice> load(String id);

  List<StoredInvoice> list();
}
