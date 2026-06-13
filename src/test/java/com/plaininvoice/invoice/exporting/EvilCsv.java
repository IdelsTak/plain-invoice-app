package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.printable.*;
import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.time.*;
import java.util.*;

final class EvilCsv {

  private EvilCsv() {}

  static InvoiceDocument document() {
    return new InvoiceDocument(
      new DocumentMeta("Invoice", "en"),
      new DocumentHeader("INV-CSV", LocalDate.of(2026, 1, 1), "Draft"),
      parties(),
      lines(),
      totals(),
      new DocumentTerms(LocalDate.of(2026, 1, 31), "")
    );
  }

  private static DocumentParties parties() {
    return new DocumentParties(
      new DocumentParty("Comma, value", "", "seller@example.com"),
      new DocumentParty("Café Buyer", "Quote \" value", "buyer@example.com")
    );
  }

  private static List<DocumentLine> lines() {
    return List.of(new DocumentLine(1, "Line\nvalue", quantity(), percent(), amounts()));
  }

  private static DocumentAmounts amounts() {
    var money = money();
    return new DocumentAmounts(money, money, money, money);
  }

  private static DocumentTotals totals() {
    var money = money();
    return new DocumentTotals(money, money, money);
  }

  private static Money money() {
    return new Money(new BigDecimal("1.00"), new CurrencyCode("USD"));
  }

  private static Quantity quantity() {
    return new Quantity(BigDecimal.ONE);
  }

  private static Percentage percent() {
    return new Percentage(BigDecimal.ZERO);
  }
}
