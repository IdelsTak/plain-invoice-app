package com.plaininvoice.invoice.totals;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class LineTaxTest {

  @Test
  void computesLineTax() {
    var lineTax = new LineTax();
    var tax = lineTax.tax(line(new BigDecimal("2"), new BigDecimal("10.00"), new BigDecimal("16")));
    assertThat(tax.amount(), comparesEqualTo(new BigDecimal("3.20")));
  }

  @Test
  void computesInvoiceTaxBySummingLines() {
    var lineTax = new LineTax();
    var tax = lineTax.total(invoice(
      line(new BigDecimal("2"), new BigDecimal("10.00"), new BigDecimal("16")),
      line(new BigDecimal("1"), new BigDecimal("5.00"), new BigDecimal("16"))
    ));
    assertThat(tax.amount(), comparesEqualTo(new BigDecimal("4.00")));
  }

  @Test
  void appliesDeterministicRoundingForFractionalTax() {
    var lineTax = new LineTax();
    var tax = lineTax.tax(line(new BigDecimal("1"), new BigDecimal("0.05"), new BigDecimal("10")));
    assertThat(tax.amount(), comparesEqualTo(new BigDecimal("0.01")));
  }

  @Test
  void rejectsNullInvoice() {
    var lineTax = new LineTax();
    assertThrows(NullPointerException.class, () -> lineTax.total(null));
  }

  @Test
  void rejectsNullLine() {
    var lineTax = new LineTax();
    assertThrows(NullPointerException.class, () -> lineTax.tax(null));
  }

  @Test
  void rejectsNullPolicy() {
    assertThrows(NullPointerException.class, () -> new LineTax(null));
  }

  private Invoice invoice(LineItem... lines) {
    return new Invoice(
      "INV-2001",
      new Party("Seller Ltd", "TAX-01", "seller@example.com"),
      new Party("Buyer LLC", "TAX-02", "buyer@example.com"),
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(lines),
      new InvoiceState.Draft()
    );
  }

  private LineItem line(BigDecimal quantity, BigDecimal price, BigDecimal rate) {
    return new LineItem(
      "Service",
      new Quantity(quantity),
      new Money(price, new CurrencyCode("USD")),
      new Percentage(rate)
    );
  }
}
