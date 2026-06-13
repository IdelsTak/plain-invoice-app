package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.printable.*;
import com.plaininvoice.invoice.pricing.*;
import java.nio.charset.*;
import java.time.*;
import java.util.*;

public final class BuildCsv implements CsvPort {

  private static final String BREAK = "\r\n";

  @Override
  public CsvFile csv(InvoiceDocument document) {
    Objects.requireNonNull(document, "invoice document cannot be null");
    var out = new StringBuilder();
    row(out, "invoice_number", "issued_on", "due_date", "payment_terms", "seller_name", "seller_tax_id", "buyer_name", "buyer_tax_id", "line_number", "description", "quantity", "tax_rate", "currency", "unit_price", "subtotal", "tax", "total", "invoice_total");
    for (var line : document.lines()) {
      line(out, document, line);
    }
    return new CsvFile(out.toString(), StandardCharsets.UTF_8);
  }

  private void line(StringBuilder out, InvoiceDocument document, DocumentLine line) {
    row(
      out,
      document.header().number(),
      date(document.header().issuedOn()),
      date(document.terms().dueDate()),
      document.terms().note(),
      document.parties().seller().name(),
      document.parties().seller().taxId(),
      document.parties().buyer().name(),
      document.parties().buyer().taxId(),
      Integer.toString(line.position()),
      line.description(),
      quantity(line.quantity()),
      percent(line.taxRate()),
      line.amounts().total().currencyCode().value(),
      money(line.amounts().unitPrice()),
      money(line.amounts().subtotal()),
      money(line.amounts().tax()),
      money(line.amounts().total()),
      money(document.totals().totalDue())
    );
  }

  private void row(StringBuilder out, String... fields) {
    for (var i = 0; i < fields.length; i++) {
      if (i > 0) {
        out.append(",");
      }
      out.append(CsvText.field(fields[i]));
    }
    out.append(BREAK);
  }

  private String money(Money value) {
    return value.amount().toPlainString();
  }

  private String quantity(Quantity value) {
    return value.value().toPlainString();
  }

  private String percent(Percentage value) {
    return value.value().toPlainString();
  }

  private String date(LocalDate value) {
    return value.toString();
  }
}
