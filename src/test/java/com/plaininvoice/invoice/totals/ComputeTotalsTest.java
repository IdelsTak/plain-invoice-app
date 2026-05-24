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

final class ComputeTotalsTest {

  @Test
  void computesSubtotal() {
    var useCase = new ComputeTotals();
    var totals = useCase.execute(new ComputeTotalsRequest(sampleInvoice()));
    assertThat(totals.subtotal().amount(), comparesEqualTo(new BigDecimal("25.00")));
  }

  @Test
  void computesTotalTax() {
    var useCase = new ComputeTotals();
    var totals = useCase.execute(new ComputeTotalsRequest(sampleInvoice()));
    assertThat(totals.totalTax().amount(), comparesEqualTo(new BigDecimal("4.00")));
  }

  @Test
  void computesTotalDue() {
    var useCase = new ComputeTotals();
    var totals = useCase.execute(new ComputeTotalsRequest(sampleInvoice()));
    assertThat(totals.totalDue().amount(), comparesEqualTo(new BigDecimal("29.00")));
  }

  @Test
  void rejectsNullRequest() {
    var useCase = new ComputeTotals();
    assertThrows(NullPointerException.class, () -> useCase.execute(null));
  }

  @Test
  void prefersLineFirstTaxForRoundingEdgeCase() {
    var useCase = new ComputeTotals();
    var totals = useCase.execute(new ComputeTotalsRequest(roundingEdgeInvoice()));
    assertThat(totals.totalTax().amount(), comparesEqualTo(new BigDecimal("0.02")));
  }

  private Invoice sampleInvoice() {
    var seller = new Party("Seller Ltd", "TAX-01", "seller@example.com");
    var buyer = new Party("Buyer LLC", "TAX-02", "buyer@example.com");

    var item1 = new LineItem(
      "Service A",
      new Quantity(new BigDecimal("2")),
      new Money(new BigDecimal("10.00"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("16"))
    );

    var item2 = new LineItem(
      "Service B",
      new Quantity(new BigDecimal("1")),
      new Money(new BigDecimal("5.00"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("16"))
    );

    return new Invoice(
      "INV-2010",
      seller,
      buyer,
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(item1, item2),
      new InvoiceState.Draft()
    );
  }

  private Invoice roundingEdgeInvoice() {
    var seller = new Party("Seller Ltd", "TAX-01", "seller@example.com");
    var buyer = new Party("Buyer LLC", "TAX-02", "buyer@example.com");

    var item1 = new LineItem(
      "Micro A",
      new Quantity(new BigDecimal("1")),
      new Money(new BigDecimal("0.05"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("10"))
    );

    var item2 = new LineItem(
      "Micro B",
      new Quantity(new BigDecimal("1")),
      new Money(new BigDecimal("0.05"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("10"))
    );

    return new Invoice(
      "INV-2011",
      seller,
      buyer,
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(item1, item2),
      new InvoiceState.Draft()
    );
  }
}
