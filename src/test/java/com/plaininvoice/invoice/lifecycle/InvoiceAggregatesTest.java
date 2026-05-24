package com.plaininvoice.invoice.lifecycle;

import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class InvoiceAggregatesTest {

  @Test
  void computesInvoiceTotalsFromLineItems() {
    var invoice = sampleInvoice();
    assertThat(invoice.subtotal().amount(), comparesEqualTo(new BigDecimal("25.00")));
    assertThat(invoice.totalTax().amount(), comparesEqualTo(new BigDecimal("4.00")));
    assertThat(invoice.totalDue().amount(), comparesEqualTo(new BigDecimal("29.00")));
  }

  @Test
  void enforcesDueDateAfterIssueDate() {
    var seller = new Party("Seller Ltd", "TAX-01", "seller@example.com");
    var buyer = new Party("Buyer LLC", "TAX-02", "buyer@example.com");
    var line = new LineItem(
      "Consulting",
      new Quantity(new BigDecimal("1")),
      new Money(new BigDecimal("10"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("16"))
    );

    assertThrows(
      IllegalArgumentException.class,
      () -> new Invoice(
        "INV-1001",
        seller,
        buyer,
        LocalDate.of(2026, 5, 24),
        new PaymentTerms(LocalDate.of(2026, 5, 23), "Net 1"),
        List.of(line),
        InvoiceState.DRAFT
      )
    );
  }

  @Test
  void rejectsInvalidStateTransition() {
    var invoice = sampleInvoice().issue().markSent().markPaid();
    assertThrows(IllegalStateException.class, invoice::voidInvoice);
  }

  @Test
  void producesNewImmutableStateOnTransitions() {
    var draft = sampleInvoice();
    var issued = draft.issue();
    var sent = issued.markSent();

    assertThat(draft.state(), is(InvoiceState.DRAFT));
    assertThat(issued.state(), is(InvoiceState.ISSUED));
    assertThat(sent.state(), is(InvoiceState.SENT));
  }

  @Test
  void rejectsMismatchedTaxBreakdown() {
    var base = new Money(new BigDecimal("100.00"), new CurrencyCode("USD"));
    var invalidTax = new Money(new BigDecimal("10.00"), new CurrencyCode("USD"));

    assertThrows(
      IllegalArgumentException.class,
      () -> new TaxBreakdown(new Percentage(new BigDecimal("16")), base, invalidTax)
    );
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
      "INV-1000",
      seller,
      buyer,
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(item1, item2),
      InvoiceState.DRAFT
    );
  }
}
