package com.plaininvoice.invoice.editing;

import com.plaininvoice.invoice.draft.*;
import com.plaininvoice.invoice.lifecycle.*;
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
    var useCase = new EditInvoice();
    var edited = useCase.execute(validRequest(draftInvoice(), "INV-2001-REV"));
    assertThat(edited.invoice().number(), is("INV-2001-REV"));
  }

  @Test
  void rejectsEditingIssuedInvoice() {
    var useCase = new EditInvoice();
    assertThrows(
      IllegalStateException.class,
      () -> useCase.execute(validRequest(draftInvoice().issue(), "INV-2001-REV"))
    );
  }

  @Test
  void rejectsNullRequest() {
    var useCase = new EditInvoice();
    assertThrows(NullPointerException.class, () -> useCase.execute(null));
  }

  private EditInvoiceRequest validRequest(Invoice current, String number) {
    var seller = new Party("Seller Ltd", "TAX-01", "seller@example.com");
    var buyer = new Party("Buyer LLC", "TAX-02", "buyer@example.com");
    var line = new LineItem(
      "Updated service",
      new Quantity(new BigDecimal("3")),
      new Money(new BigDecimal("12.00"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("16"))
    );

    var spec = new InvoiceDraftSpec(
      new InvoiceIdentity(number),
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
      "INV-2001",
      seller,
      buyer,
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(line),
      new InvoiceState.Draft()
    );
  }
}
