package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.pagination.*;

public interface HtmlPort {
  HtmlPage html(PageDocument pages);
}
