package com.plaininvoice.invoice.listing;

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

final class ListInvoicesTest {

  @Test
  void rejectsNullRepository() {
    assertThrows(NullPointerException.class, () -> new ListInvoices(null));
  }

  @Test
  void listsEmptyRepository() {
    var result = new ListInvoices(new Repo(List.of())).execute();
    assertThat(result.invoices(), is(empty()));
  }

  @Test
  void listsInvoiceSummary() {
    var result = new ListInvoices(new Repo(List.of(stored("inv-1", 1, draft())))).execute();
    assertThat(result.invoices().getFirst().number(), is("CORE-00001"));
  }

  @Test
  void listsInvoiceId() {
    var result = new ListInvoices(new Repo(List.of(stored("inv-1", 1, draft())))).execute();
    assertThat(result.invoices().getFirst().id(), is("inv-1"));
  }

  @Test
  void listsInvoiceVersion() {
    var result = new ListInvoices(new Repo(List.of(stored("inv-1", 2, draft())))).execute();
    assertThat(result.invoices().getFirst().version(), is(2L));
  }

  @Test
  void listsBuyerName() {
    var result = new ListInvoices(new Repo(List.of(stored("inv-1", 1, draft())))).execute();
    assertThat(result.invoices().getFirst().buyerName(), is("Buyer LLC"));
  }

  @Test
  void listsIssueDate() {
    var result = new ListInvoices(new Repo(List.of(stored("inv-1", 1, draft())))).execute();
    assertThat(result.invoices().getFirst().issuedOn(), is(LocalDate.of(2026, 5, 24)));
  }

  @Test
  void listsDueDate() {
    var result = new ListInvoices(new Repo(List.of(stored("inv-1", 1, draft())))).execute();
    assertThat(result.invoices().getFirst().dueDate(), is(LocalDate.of(2026, 6, 24)));
  }

  @Test
  void listsTotalDue() {
    var result = new ListInvoices(new Repo(List.of(stored("inv-1", 1, draft())))).execute();
    assertThat(result.invoices().getFirst().totalDue().amount(), comparesEqualTo(new BigDecimal("11.60")));
  }

  @Test
  void mapsDraftState() {
    assertThat(summary(new InvoiceState.Draft()).state(), is("DRAFT"));
  }

  @Test
  void mapsIssuedState() {
    assertThat(summary(new InvoiceState.Issued()).state(), is("ISSUED"));
  }

  @Test
  void mapsSentState() {
    assertThat(summary(new InvoiceState.Sent()).state(), is("SENT"));
  }

  @Test
  void mapsPaidState() {
    assertThat(summary(new InvoiceState.Paid()).state(), is("PAID"));
  }

  @Test
  void mapsVoidState() {
    assertThat(summary(new InvoiceState.Void()).state(), is("VOID"));
  }

  @Test
  void rejectsBlankSummaryId() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new InvoiceSummary(" ", 1, "CORE-00001", "Buyer LLC", LocalDate.now(), LocalDate.now(), "DRAFT", money("1.00"))
    );
  }

  @Test
  void rejectsNullSummarySource() {
    assertThrows(NullPointerException.class, () -> InvoiceSummary.from(null));
  }

  @Test
  void rejectsNullResultList() {
    assertThrows(NullPointerException.class, () -> new ListInvoicesResult(null));
  }

  @Test
  void copiesResultList() {
    var source = new ArrayList<InvoiceSummary>();
    var result = new ListInvoicesResult(source);
    source.add(summary(new InvoiceState.Draft()));
    assertThat(result.invoices(), is(empty()));
  }

  private InvoiceSummary summary(InvoiceState state) {
    return InvoiceSummary.from(stored("inv-1", 1, invoice(state)));
  }

  private StoredInvoice stored(String id, long version, Invoice invoice) {
    var meta = new InvoiceStoreMeta(
      new InvoiceStoreKey(id, version),
      new StoreClock(Instant.parse("2026-05-24T00:00:00Z"), Instant.parse("2026-05-24T00:00:00Z")),
      Optional.empty()
    );
    return new StoredInvoice(meta, invoice);
  }

  private Invoice draft() {
    return invoice(new InvoiceState.Draft());
  }

  private Invoice invoice(InvoiceState state) {
    return new Invoice(
      "CORE-00001",
      new Party("Seller Ltd", "TAX-01", "seller@example.com"),
      new Party("Buyer LLC", "TAX-02", "buyer@example.com"),
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(line()),
      state
    );
  }

  private LineItem line() {
    return new LineItem("Service", new Quantity(new BigDecimal("1")), money("10.00"), new Percentage(new BigDecimal("16")));
  }

  private Money money(String amount) {
    return new Money(new BigDecimal(amount), new CurrencyCode("USD"));
  }

  private record Repo(List<StoredInvoice> invoices) implements InvoiceRepository {
    @Override
    public StoredInvoice save(StoredInvoice invoice) {
      throw new UnsupportedOperationException("save");
    }

    @Override
    public Optional<StoredInvoice> load(String id) {
      throw new UnsupportedOperationException("load");
    }

    @Override
    public List<StoredInvoice> list() {
      return invoices;
    }
  }
}
