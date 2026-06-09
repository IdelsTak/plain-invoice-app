package com.plaininvoice.invoice.creation;

import com.plaininvoice.invoice.draft.*;
import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.numbering.*;
import com.plaininvoice.invoice.pricing.*;
import com.plaininvoice.invoice.storage.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class CreateStoredInvoiceTest {

  @Test
  void rejectsNullCreate() {
    assertThrows(NullPointerException.class, () -> new CreateStoredInvoice(null, new Repo(false)));
  }

  @Test
  void rejectsNullRepository() {
    assertThrows(NullPointerException.class, () -> new CreateStoredInvoice(create(), null));
  }

  @Test
  void rejectsNullRequest() {
    var useCase = new CreateStoredInvoice(create(), new Repo(false));
    assertThrows(NullPointerException.class, () -> useCase.execute(null));
  }

  @Test
  void rejectsNullRequestId() {
    assertThrows(NullPointerException.class, () -> new CreateStoredInvoiceRequest(null, clock(), request()));
  }

  @Test
  void rejectsBlankRequestId() {
    assertThrows(IllegalArgumentException.class, () -> new CreateStoredInvoiceRequest(" ", clock(), request()));
  }

  @Test
  void rejectsNullClock() {
    assertThrows(NullPointerException.class, () -> new CreateStoredInvoiceRequest("inv-1", null, request()));
  }

  @Test
  void rejectsNullInvoiceRequest() {
    assertThrows(NullPointerException.class, () -> new CreateStoredInvoiceRequest("inv-1", clock(), null));
  }

  @Test
  void trimsRequestId() {
    assertThat(new CreateStoredInvoiceRequest(" inv-1 ", clock(), request()).id(), is("inv-1"));
  }

  @Test
  void savesCreatedInvoice() {
    var result = new CreateStoredInvoice(create(), new Repo(false)).execute(storedRequest());
    assertThat(((CreateStoredInvoiceResult.Saved) result).invoice().invoice().number(), is("CORE-00001"));
  }

  @Test
  void savesWithVersionOne() {
    var result = new CreateStoredInvoice(create(), new Repo(false)).execute(storedRequest());
    assertThat(((CreateStoredInvoiceResult.Saved) result).invoice().meta().key().version(), is(1L));
  }

  @Test
  void savesWithStorageId() {
    var result = new CreateStoredInvoice(create(), new Repo(false)).execute(storedRequest());
    assertThat(((CreateStoredInvoiceResult.Saved) result).invoice().meta().key().id(), is("inv-1"));
  }

  @Test
  void returnsConflict() {
    var result = new CreateStoredInvoice(create(), new Repo(true)).execute(storedRequest());
    assertThat(result, instanceOf(CreateStoredInvoiceResult.Conflict.class));
  }

  @Test
  void returnsConflictReason() {
    var result = (CreateStoredInvoiceResult.Conflict) new CreateStoredInvoice(create(), new Repo(true)).execute(storedRequest());
    assertThat(result.reason(), is("version conflict"));
  }

  @Test
  void rejectsNullSavedInvoice() {
    assertThrows(NullPointerException.class, () -> new CreateStoredInvoiceResult.Saved(null));
  }

  @Test
  void rejectsNullConflictId() {
    assertThrows(NullPointerException.class, () -> new CreateStoredInvoiceResult.Conflict(null, "conflict"));
  }

  @Test
  void rejectsBlankConflictId() {
    assertThrows(IllegalArgumentException.class, () -> new CreateStoredInvoiceResult.Conflict(" ", "conflict"));
  }

  @Test
  void trimsConflictReason() {
    assertThat(new CreateStoredInvoiceResult.Conflict("inv-1", " conflict ").reason(), is("conflict"));
  }

  @Test
  void toleratesNullConflictReason() {
    assertThat(new CreateStoredInvoiceResult.Conflict("inv-1", null).reason(), is(""));
  }

  private CreateInvoice create() {
    return new CreateInvoice(new MemoryInvoiceNumberUniqueness(new HashSet<>()));
  }

  private CreateStoredInvoiceRequest storedRequest() {
    return new CreateStoredInvoiceRequest("inv-1", clock(), request());
  }

  private CreateInvoiceRequest request() {
    return new CreateInvoiceRequest(spec("CORE", 1));
  }

  private InvoiceDraftSpec spec(String series, long sequence) {
    return new InvoiceDraftSpec(
      new InvoiceIdentity(new InvoiceNumber(series, sequence)),
      new InvoiceParties(new Party("Seller Ltd", "TAX-01", "seller@example.com"), new Party("Buyer LLC", "TAX-02", "buyer@example.com")),
      new InvoiceSchedule(LocalDate.of(2026, 5, 24), new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30")),
      new InvoiceLines(List.of(line()))
    );
  }

  private LineItem line() {
    return new LineItem("Service", new Quantity(new BigDecimal("1")), money("10.00"), new Percentage(new BigDecimal("16")));
  }

  private Money money(String amount) {
    return new Money(new BigDecimal(amount), new CurrencyCode("USD"));
  }

  private StoreClock clock() {
    return new StoreClock(Instant.parse("2026-05-24T00:00:00Z"), Instant.parse("2026-05-24T00:00:00Z"));
  }

  private record Repo(boolean conflict) implements InvoiceRepository {
    @Override
    public StoredInvoice save(StoredInvoice invoice) {
      if (conflict) {
        throw new StoreConflict("version conflict");
      }
      var key = new InvoiceStoreKey(invoice.meta().key().id(), 1);
      var meta = new InvoiceStoreMeta(key, invoice.meta().clock(), invoice.meta().voidMark());
      return new StoredInvoice(meta, invoice.invoice());
    }

    @Override
    public Optional<StoredInvoice> load(String id) {
      throw new UnsupportedOperationException("load");
    }

    @Override
    public List<StoredInvoice> list() {
      throw new UnsupportedOperationException("list");
    }
  }
}
