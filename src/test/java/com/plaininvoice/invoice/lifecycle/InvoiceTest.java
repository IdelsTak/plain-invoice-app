package com.plaininvoice.invoice.lifecycle;

import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class InvoiceTest {

  @Test
  void computesSubtotalFromLineItems() {
    var invoice = sampleInvoice();
    assertThat(invoice.subtotal().amount(), comparesEqualTo(new BigDecimal("25.00")));
  }

  @Test
  void computesTotalTaxFromLineItems() {
    var invoice = sampleInvoice();
    assertThat(invoice.totalTax().amount(), comparesEqualTo(new BigDecimal("4.00")));
  }

  @Test
  void computesTotalDueFromLineItems() {
    var invoice = sampleInvoice();
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
        new InvoiceState.Draft()
      )
    );
  }

  @Test
  void rejectsBlankInvoiceNumber() {
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
        "  ",
        seller,
        buyer,
        LocalDate.of(2026, 5, 24),
        new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
        List.of(line),
        new InvoiceState.Draft()
      )
    );
  }

  @Test
  void rejectsInvoiceWithoutLineItems() {
    var seller = new Party("Seller Ltd", "TAX-01", "seller@example.com");
    var buyer = new Party("Buyer LLC", "TAX-02", "buyer@example.com");

    assertThrows(
      IllegalArgumentException.class,
      () -> new Invoice(
        "INV-1002",
        seller,
        buyer,
        LocalDate.of(2026, 5, 24),
        new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
        List.of(),
        new InvoiceState.Draft()
      )
    );
  }

  @Test
  void rejectsVoidingPaidInvoice() {
    var invoice = sampleInvoice().issue().markSent().markPaid();
    assertThrows(IllegalStateException.class, invoice::voidInvoice);
  }

  @Test
  void startsInDraftState() {
    var draft = sampleInvoice();
    assertThat(draft.state(), is(new InvoiceState.Draft()));
  }

  @Test
  void transitionsToIssuedState() {
    var issued = sampleInvoice().issue();
    assertThat(issued.state(), is(new InvoiceState.Issued()));
  }

  @Test
  void transitionsToSentState() {
    var sent = sampleInvoice().issue().markSent();
    assertThat(sent.state(), is(new InvoiceState.Sent()));
  }

  @Test
  void transitionsToPaidStateFromIssued() {
    var paid = sampleInvoice().issue().markPaid();
    assertThat(paid.state(), is(new InvoiceState.Paid()));
  }

  @Test
  void transitionsToVoidStateFromDraft() {
    var voided = sampleInvoice().voidInvoice();
    assertThat(voided.state(), is(new InvoiceState.Void()));
  }

  @Test
  void rejectsIssueFromIssuedState() {
    var issued = sampleInvoice().issue();
    assertThrows(IllegalStateException.class, issued::issue);
  }

  @Test
  void rejectsIssueFromSentState() {
    var sent = sampleInvoice().issue().markSent();
    assertThrows(IllegalStateException.class, sent::issue);
  }

  @Test
  void rejectsIssueFromPaidState() {
    var paid = sampleInvoice().issue().markPaid();
    assertThrows(IllegalStateException.class, paid::issue);
  }

  @Test
  void rejectsIssueFromVoidState() {
    var voided = sampleInvoice().voidInvoice();
    assertThrows(IllegalStateException.class, voided::issue);
  }

  @Test
  void rejectsMarkSentFromDraftState() {
    var draft = sampleInvoice();
    assertThrows(IllegalStateException.class, draft::markSent);
  }

  @Test
  void rejectsMarkSentFromSentState() {
    var sent = sampleInvoice().issue().markSent();
    assertThrows(IllegalStateException.class, sent::markSent);
  }

  @Test
  void rejectsMarkSentFromPaidState() {
    var paid = sampleInvoice().issue().markPaid();
    assertThrows(IllegalStateException.class, paid::markSent);
  }

  @Test
  void rejectsMarkSentFromVoidState() {
    var voided = sampleInvoice().voidInvoice();
    assertThrows(IllegalStateException.class, voided::markSent);
  }

  @Test
  void rejectsMarkPaidFromDraftState() {
    var draft = sampleInvoice();
    assertThrows(IllegalStateException.class, draft::markPaid);
  }

  @Test
  void rejectsMarkPaidFromPaidState() {
    var paid = sampleInvoice().issue().markPaid();
    assertThrows(IllegalStateException.class, paid::markPaid);
  }

  @Test
  void rejectsMarkPaidFromVoidState() {
    var voided = sampleInvoice().voidInvoice();
    assertThrows(IllegalStateException.class, voided::markPaid);
  }

  @Test
  void transitionsToVoidStateFromIssued() {
    var voided = sampleInvoice().issue().voidInvoice();
    assertThat(voided.state(), is(new InvoiceState.Void()));
  }

  @Test
  void transitionsToVoidStateFromSent() {
    var voided = sampleInvoice().issue().markSent().voidInvoice();
    assertThat(voided.state(), is(new InvoiceState.Void()));
  }

  @Test
  void transitionsToVoidStateFromVoidState() {
    var voided = sampleInvoice().voidInvoice().voidInvoice();
    assertThat(voided.state(), is(new InvoiceState.Void()));
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
      new InvoiceState.Draft()
    );
  }
}
