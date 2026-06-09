package com.plaininvoice.invoice.editing;

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

final class EditStoredInvoiceTest {

  @Test
  void rejectsNullEdit() {
    assertThrows(NullPointerException.class, () -> new EditStoredInvoice(null, new Repo(false)));
  }

  @Test
  void rejectsNullRepository() {
    assertThrows(NullPointerException.class, () -> new EditStoredInvoice(edit(), null));
  }

  @Test
  void rejectsNullRequest() {
    var useCase = new EditStoredInvoice(edit(), new Repo(false));
    assertThrows(NullPointerException.class, () -> useCase.execute(null));
  }

  @Test
  void rejectsNullCurrent() {
    assertThrows(NullPointerException.class, () -> new EditStoredInvoiceRequest(null, spec("CORE", 1), updatedAt()));
  }

  @Test
  void rejectsNullSpec() {
    assertThrows(NullPointerException.class, () -> new EditStoredInvoiceRequest(stored(1), null, updatedAt()));
  }

  @Test
  void rejectsNullUpdatedAt() {
    assertThrows(NullPointerException.class, () -> new EditStoredInvoiceRequest(stored(1), spec("CORE", 1), null));
  }

  @Test
  void savesEditedInvoice() {
    var result = new EditStoredInvoice(edit(), new Repo(false)).execute(request(1));
    assertThat(((EditStoredInvoiceResult.Saved) result).invoice().invoice().number(), is("CORE-00001"));
  }

  @Test
  void savesNextVersion() {
    var result = new EditStoredInvoice(edit(), new Repo(false)).execute(request(1));
    assertThat(((EditStoredInvoiceResult.Saved) result).invoice().meta().key().version(), is(2L));
  }

  @Test
  void keepsCreatedAt() {
    var result = new EditStoredInvoice(edit(), new Repo(false)).execute(request(1));
    assertThat(((EditStoredInvoiceResult.Saved) result).invoice().meta().clock().createdAt(), is(createdAt()));
  }

  @Test
  void updatesUpdatedAt() {
    var result = new EditStoredInvoice(edit(), new Repo(false)).execute(request(1));
    assertThat(((EditStoredInvoiceResult.Saved) result).invoice().meta().clock().updatedAt(), is(updatedAt()));
  }

  @Test
  void returnsConflict() {
    var result = new EditStoredInvoice(edit(), new Repo(true)).execute(request(1));
    assertThat(result, instanceOf(EditStoredInvoiceResult.Conflict.class));
  }

  @Test
  void returnsConflictId() {
    var result = (EditStoredInvoiceResult.Conflict) new EditStoredInvoice(edit(), new Repo(true)).execute(request(1));
    assertThat(result.id(), is("inv-1"));
  }

  @Test
  void rejectsNullSavedInvoice() {
    assertThrows(NullPointerException.class, () -> new EditStoredInvoiceResult.Saved(null));
  }

  @Test
  void rejectsNullConflictId() {
    assertThrows(NullPointerException.class, () -> new EditStoredInvoiceResult.Conflict(null, "conflict"));
  }

  @Test
  void rejectsBlankConflictId() {
    assertThrows(IllegalArgumentException.class, () -> new EditStoredInvoiceResult.Conflict(" ", "conflict"));
  }

  @Test
  void trimsConflictReason() {
    assertThat(new EditStoredInvoiceResult.Conflict("inv-1", " conflict ").reason(), is("conflict"));
  }

  @Test
  void toleratesNullConflictReason() {
    assertThat(new EditStoredInvoiceResult.Conflict("inv-1", null).reason(), is(""));
  }

  private EditStoredInvoiceRequest request(long version) {
    return new EditStoredInvoiceRequest(stored(version), spec("CORE", 1), updatedAt());
  }

  private EditInvoice edit() {
    return new EditInvoice(new MemoryInvoiceNumberUniqueness(new HashSet<>()), new InvoiceNumberScan());
  }

  private StoredInvoice stored(long version) {
    var meta = new InvoiceStoreMeta(new InvoiceStoreKey("inv-1", version), new StoreClock(createdAt(), createdAt()), Optional.empty());
    return new StoredInvoice(meta, invoice());
  }

  private Invoice invoice() {
    return new Invoice(
      "CORE-00001",
      new Party("Seller Ltd", "TAX-01", "seller@example.com"),
      new Party("Buyer LLC", "TAX-02", "buyer@example.com"),
      LocalDate.of(2026, 5, 24),
      new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30"),
      List.of(line("Service")),
      new InvoiceState.Draft()
    );
  }

  private InvoiceDraftSpec spec(String series, long sequence) {
    return new InvoiceDraftSpec(
      new InvoiceIdentity(new InvoiceNumber(series, sequence)),
      new InvoiceParties(new Party("Seller Ltd", "TAX-01", "seller@example.com"), new Party("Buyer LLC", "TAX-02", "buyer@example.com")),
      new InvoiceSchedule(LocalDate.of(2026, 5, 24), new PaymentTerms(LocalDate.of(2026, 6, 24), "Net 30")),
      new InvoiceLines(List.of(line("Updated service")))
    );
  }

  private LineItem line(String description) {
    return new LineItem(description, new Quantity(new BigDecimal("1")), money("10.00"), new Percentage(new BigDecimal("16")));
  }

  private Money money(String amount) {
    return new Money(new BigDecimal(amount), new CurrencyCode("USD"));
  }

  private Instant createdAt() {
    return Instant.parse("2026-05-24T00:00:00Z");
  }

  private Instant updatedAt() {
    return Instant.parse("2026-05-25T00:00:00Z");
  }

  private record Repo(boolean conflict) implements InvoiceRepository {
    @Override
    public StoredInvoice save(StoredInvoice invoice) {
      if (conflict) {
        throw new StoreConflict("version conflict");
      }
      var version = invoice.meta().key().version() + 1;
      var key = new InvoiceStoreKey(invoice.meta().key().id(), version);
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
