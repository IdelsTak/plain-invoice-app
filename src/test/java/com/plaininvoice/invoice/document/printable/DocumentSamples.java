package com.plaininvoice.invoice.document.printable;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.time.*;
import java.util.*;

public final class DocumentSamples {

  private DocumentSamples() {}

  public static DocumentMeta meta() {
    return new DocumentMeta("Invoice", "en");
  }

  public static Invoice invoice() {
    return new Invoice(
      "INV-1000",
      new Party("Seller Ltd", "TAX-01", "seller@example.com"),
      new Party("Buyer LLC", "TAX-02", "buyer@example.com"),
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(line("Service A", "2", "10.00"), line("Service B", "1", "5.00")),
      new InvoiceState.Draft()
    );
  }

  public static DocumentHeader header() {
    return new DocumentHeader("INV-1000", LocalDate.of(2026, 5, 24), "Draft");
  }

  public static DocumentParty party(String name) {
    return new DocumentParty(name, "TAX", "party@example.com");
  }

  public static DocumentParties parties() {
    return new DocumentParties(party("Seller"), party("Buyer"));
  }

  public static List<DocumentLine> lines() {
    return List.of(new DocumentLine(1, "Service", quantity("2"), percent("16"), amounts()));
  }

  public static DocumentAmounts amounts() {
    return new DocumentAmounts(money("10.00"), money("20.00"), money("3.20"), money("23.20"));
  }

  public static DocumentTotals totals() {
    return new DocumentTotals(money("20.00"), money("3.20"), money("23.20"));
  }

  public static DocumentTerms terms() {
    return new DocumentTerms(LocalDate.of(2026, 6, 24), "Net 30");
  }

  public static Money money(String amount) {
    return new Money(new BigDecimal(amount), new CurrencyCode("USD"));
  }

  public static Quantity quantity(String value) {
    return new Quantity(new BigDecimal(value));
  }

  public static Percentage percent(String value) {
    return new Percentage(new BigDecimal(value));
  }

  private static LineItem line(String description, String quantity, String unitPrice) {
    return new LineItem(description, quantity(quantity), money(unitPrice), percent("16"));
  }
}
