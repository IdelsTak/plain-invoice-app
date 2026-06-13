package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.printable.*;
import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.time.*;
import java.util.*;

final class ExportCases {

  private static final LocalDate ISSUED_ON = LocalDate.of(2026, 5, 24);
  private static final LocalDate DUE_ON = LocalDate.of(2026, 6, 24);

  private ExportCases() {}

  static List<ExportCase> all() {
    return List.of(simple(), multiLine(), taxed(), rounding(), longContent());
  }

  static ExportCase simple() {
    return exported(
      "simple",
      "single untaxed line for base document shape",
      List.of(line("Consulting session", "1", "120.00", "0"))
    );
  }

  static ExportCase multiLine() {
    return exported(
      "multi-line",
      "several ordered lines for table and pagination checks",
      List.of(
        line("Discovery", "1", "120.00", "0"),
        line("Implementation", "2", "250.00", "16"),
        line("Review", "3", "80.00", "16"),
        line("Training", "1", "150.00", "0")
      )
    );
  }

  static ExportCase taxed() {
    return exported(
      "taxed",
      "tax-bearing line for totals and tax display checks",
      List.of(line("VAT rated service", "2", "100.00", "16"))
    );
  }

  static ExportCase rounding() {
    return exported(
      "rounding",
      "fractional quantity for monetary rounding checks",
      List.of(line("Metered support", "1.333", "10.00", "7.5"))
    );
  }

  static ExportCase longContent() {
    return exported(
      "long-content",
      "long party and line text for wrapping and page-frame checks",
      List.of(line("Extended implementation advisory with migration notes and follow-up review", "1", "500.00", "16"))
    );
  }

  private static ExportCase exported(String key, String purpose, List<LineItem> lines) {
    return new ExportCase(key, purpose, invoice(lines), new DocumentMeta("Invoice", "en"));
  }

  private static Invoice invoice(List<LineItem> lines) {
    return new Invoice(
      "INV-" + ISSUED_ON.getYear() + "-1000",
      new Party("Plain Invoice Studio Ltd", "SELLER-TAX-01", "seller@example.com"),
      new Party("Acme Buyer Operations LLC", "BUYER-TAX-02", "buyer@example.com"),
      ISSUED_ON,
      new PaymentTerms(DUE_ON, "Net 30"),
      lines,
      new InvoiceState.Draft()
    );
  }

  private static LineItem line(String description, String quantity, String unitPrice, String tax) {
    return new LineItem(description, quantity(quantity), money(unitPrice), percent(tax));
  }

  private static Quantity quantity(String value) {
    return new Quantity(new BigDecimal(value));
  }

  private static Money money(String amount) {
    return new Money(new BigDecimal(amount), new CurrencyCode("USD"));
  }

  private static Percentage percent(String value) {
    return new Percentage(new BigDecimal(value));
  }
}
