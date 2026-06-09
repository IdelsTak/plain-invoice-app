package com.plaininvoice.invoice.loading;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import com.plaininvoice.invoice.storage.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class LoadInvoiceTest {

  @Test
  void rejectsNullRepository() {
    assertThrows(NullPointerException.class, () -> new LoadInvoice(null));
  }

  @Test
  void rejectsNullRequest() {
    var useCase = new LoadInvoice(new Repo(Optional.empty()));
    assertThrows(NullPointerException.class, () -> useCase.execute(null));
  }

  @Test
  void rejectsNullRequestId() {
    assertThrows(NullPointerException.class, () -> new LoadInvoiceRequest(null));
  }

  @Test
  void rejectsBlankRequestId() {
    assertThrows(IllegalArgumentException.class, () -> new LoadInvoiceRequest(" "));
  }

  @Test
  void trimsRequestId() {
    assertThat(new LoadInvoiceRequest(" inv-1 ").id(), is("inv-1"));
  }

  @Test
  void loadsMissingInvoice() {
    var result = new LoadInvoice(new Repo(Optional.empty())).execute(new LoadInvoiceRequest("inv-1"));
    assertThat(result.invoice(), is(Optional.empty()));
  }

  @Test
  void loadsInvoiceId() {
    var result = load(stored("inv-1", 1));
    assertThat(result.invoice().orElseThrow().id(), is("inv-1"));
  }

  @Test
  void loadsInvoiceVersion() {
    var result = load(stored("inv-1", 2));
    assertThat(result.invoice().orElseThrow().version(), is(2L));
  }

  @Test
  void loadsInvoiceBody() {
    var result = load(stored("inv-1", 1));
    assertThat(result.invoice().orElseThrow().invoice().number(), is("CORE-00001"));
  }

  @Test
  void rejectsBlankDetailId() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceDetail(" ", 1, invoice()));
  }

  @Test
  void rejectsNullDetailInvoice() {
    assertThrows(NullPointerException.class, () -> new InvoiceDetail("inv-1", 1, null));
  }

  @Test
  void rejectsNullDetailSource() {
    assertThrows(NullPointerException.class, () -> InvoiceDetail.from(null));
  }

  @Test
  void rejectsNullResult() {
    assertThrows(NullPointerException.class, () -> new LoadInvoiceResult(null));
  }

  private LoadInvoiceResult load(StoredInvoice stored) {
    return new LoadInvoice(new Repo(Optional.of(stored))).execute(new LoadInvoiceRequest(stored.meta().key().id()));
  }

  private StoredInvoice stored(String id, long version) {
    var meta = new InvoiceStoreMeta(
      new InvoiceStoreKey(id, version),
      new StoreClock(Instant.parse("2026-05-24T00:00:00Z"), Instant.parse("2026-05-24T00:00:00Z")),
      Optional.empty()
    );
    return new StoredInvoice(meta, invoice());
  }

  private Invoice invoice() {
    return new Invoice(
      "CORE-00001",
      new Party("Seller Ltd", "TAX-01", "seller@example.com"),
      new Party("Buyer LLC", "TAX-02", "buyer@example.com"),
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(line()),
      new InvoiceState.Draft()
    );
  }

  private LineItem line() {
    var money = new Money(new BigDecimal("10.00"), new CurrencyCode("USD"));
    return new LineItem("Service", new Quantity(new BigDecimal("1")), money, new Percentage(new BigDecimal("16")));
  }

  private record Repo(Optional<StoredInvoice> invoice) implements InvoiceRepository {
    @Override
    public StoredInvoice save(StoredInvoice invoice) {
      throw new UnsupportedOperationException("save");
    }

    @Override
    public Optional<StoredInvoice> load(String id) {
      return invoice;
    }

    @Override
    public List<StoredInvoice> list() {
      throw new UnsupportedOperationException("list");
    }
  }
}
