package com.plaininvoice.invoice.document.pagination;

import com.plaininvoice.invoice.document.layout.*;

public interface PagePort {
  PageDocument paginate(LayoutDocument layout);
}
