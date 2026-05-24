package com.plaininvoice.invoice.editing;

import com.plaininvoice.invoice.draft.*;
import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.numbering.*;
import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class EditInvoiceTest {

  @Test
  void editsDraftInvoice() {
    var useCase = new EditInvoice(new InMemoryInvoiceNumberUniqueness(new HashSet<>()), new InvoiceNumberParser());
    var edited = useCase.execute(validRequest(draftInvoice(), "CORE", 2001));
    assertThat(edited.invoice().number(), is("CORE-02001"));
  }

  @Test
  void rejectsEditingIssuedInvoice() {
    var useCase = new EditInvoice(new InMemoryInvoiceNumberUniqueness(new HashSet<>()), new InvoiceNumberParser());
    assertThrows(
      IllegalStateException.class,
      () -> useCase.execute(validRequest(draftInvoice().issue(), "CORE", 2001))
    );
  }

  @Test
  void rejectsNullRequest() {
    var useCase = new EditInvoice(new InMemoryInvoiceNumberUniqueness(new HashSet<>()), new InvoiceNumberParser());
    assertThrows(NullPointerException.class, () -> useCase.execute(null));
  }

  @Test
  void rejectsDuplicateNumberOnEdit() {
    var uniqueness = new InMemoryInvoiceNumberUniqueness(new HashSet<>());
    var useCase = new EditInvoice(uniqueness, new InvoiceNumberParser());
    uniqueness.verify(new InvoiceNumber("CORE", 2001));
    assertThrows(IllegalArgumentException.class, () -> useCase.execute(validRequest(draftInvoice(), "CORE", 2001)));
  }

  @Test
  void allowsEditingWithoutRecheckingUnchangedNumber() {
    var uniqueness = new InMemoryInvoiceNumberUniqueness(new HashSet<>());
    uniqueness.verify(new InvoiceNumber("CORE", 2000));
    var useCase = new EditInvoice(uniqueness, new InvoiceNumberParser());
    assertDoesNotThrow(() -> useCase.execute(validRequest(draftInvoice(), "CORE", 2000)));
  }

  private EditInvoiceRequest validRequest(Invoice current, String series, long sequence) {
    var seller = new Party("Seller Ltd", "TAX-01", "seller@example.com");
    var buyer = new Party("Buyer LLC", "TAX-02", "buyer@example.com");
    var line = new LineItem(
      "Updated service",
      new Quantity(new BigDecimal("3")),
      new Money(new BigDecimal("12.00"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("16"))
    );

    var spec = new InvoiceDraftSpec(
      new InvoiceIdentity(new InvoiceNumber(series, sequence)),
      new InvoiceParties(seller, buyer),
      new InvoiceSchedule(LocalDate.of(2026, 5, 24), new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30")),
      new InvoiceLines(List.of(line))
    );

    return new EditInvoiceRequest(current, spec);
  }

  private Invoice draftInvoice() {
    var seller = new Party("Seller Ltd", "TAX-01", "seller@example.com");
    var buyer = new Party("Buyer LLC", "TAX-02", "buyer@example.com");
    var line = new LineItem(
      "Service",
      new Quantity(new BigDecimal("1")),
      new Money(new BigDecimal("10.00"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("16"))
    );

    return new Invoice(
      "CORE-02000",
      seller,
      buyer,
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(line),
      new InvoiceState.Draft()
    );
  }
}
