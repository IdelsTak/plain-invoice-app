package com.plaininvoice.invoice.creation;

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

final class CreateInvoiceTest {

  @Test
  void createsDraftInvoice() {
    var useCase = new CreateInvoice();
    var result = useCase.execute(validRequest());
    assertThat(result.invoice().state(), is(new InvoiceState.Draft()));
  }

  @Test
  void rejectsNullRequest() {
    var useCase = new CreateInvoice();
    assertThrows(NullPointerException.class, () -> useCase.execute(null));
  }

  private CreateInvoiceRequest validRequest() {
    return new CreateInvoiceRequest(validSpec("INV-2000"));
  }

  private InvoiceDraftSpec validSpec(String number) {
    var seller = new Party("Seller Ltd", "TAX-01", "seller@example.com");
    var buyer = new Party("Buyer LLC", "TAX-02", "buyer@example.com");
    var line = new LineItem(
      "Service",
      new Quantity(new BigDecimal("2")),
      new Money(new BigDecimal("10.00"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("16"))
    );

    return new InvoiceDraftSpec(
      new InvoiceIdentity(number),
      new InvoiceParties(seller, buyer),
      new InvoiceSchedule(LocalDate.of(2026, 5, 24), new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30")),
      new InvoiceLines(List.of(line))
    );
  }
}
