package com.plaininvoice.invoice.storage.sqlite;

import com.plaininvoice.invoice.storage.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

final class InvoiceMappingTest {
  private final InvoiceMapping mapping = new InvoiceMapping();

  @Test
  void mapsHeaderCurrency() {
    assertThat(rows(draft()).head().currencyCode(), is("USD"));
  }

  @Test
  void mapsLineQuantityNumerator() {
    assertThat(rows(draft()).lines().getFirst().quantity().numerator(), is(125L));
  }

  @Test
  void mapsLineQuantityDenominator() {
    assertThat(rows(draft()).lines().getFirst().quantity().denominator(), is(100L));
  }

  @Test
  void mapsLineMoneyMinorUnits() {
    assertThat(rows(draft()).lines().getFirst().price().amountMinor(), is(1200L));
  }

  @Test
  void mapsTaxRateBasisPoints() {
    assertThat(rows(draft()).taxes().getFirst().rateBps(), is(1600L));
  }

  @Test
  void mapsTaxAmountMinorUnits() {
    assertThat(rows(draft()).taxes().getFirst().amount().amountMinor(), is(240L));
  }

  @Test
  void preservesVersion() {
    assertThat(rows(draft()).head().meta().key().version(), is(7L));
  }

  @Test
  void preservesVoidMark() {
    assertThat(rows(voided()).head().data().state().voidMark(), is(Optional.of(voidMark())));
  }

  @Test
  void normalizesEmptyVoidReason() {
    assertThat(new VoidMark(now(), null).reason(), is(""));
  }

  @Test
  void restoresNumber() {
    assertThat(mapping.invoice(rows(draft())).number(), is("CORE-00001"));
  }

  @Test
  void restoresLineCount() {
    assertThat(mapping.invoice(rows(draft())).lineItems().size(), is(1));
  }

  @Test
  void restoresQuantity() {
    assertThat(mapping.invoice(rows(draft())).lineItems().getFirst().quantity().value(), comparesEqualTo(new BigDecimal("1.25")));
  }

  @Test
  void restoresMoney() {
    assertThat(mapping.invoice(rows(draft())).lineItems().getFirst().unitPrice().amount(), comparesEqualTo(new BigDecimal("12.00")));
  }

  @Test
  void restoresTaxRate() {
    assertThat(mapping.invoice(rows(draft())).lineItems().getFirst().taxRate().value(), comparesEqualTo(new BigDecimal("16")));
  }

  @Test
  void restoresOrderedLines() {
    assertThat(mapping.invoice(shuffledRows()).lineItems().getFirst().description(), is("Consulting"));
  }

  @Test
  void mapsDraftState() {
    assertThat(rows(draft()).head().data().state().code(), is("DRAFT"));
  }

  @Test
  void mapsIssuedState() {
    assertThat(rows(draft().issue()).head().data().state().code(), is("ISSUED"));
  }

  @Test
  void mapsSentState() {
    assertThat(rows(draft().issue().markSent()).head().data().state().code(), is("SENT"));
  }

  @Test
  void mapsPaidState() {
    assertThat(rows(draft().issue().markPaid()).head().data().state().code(), is("PAID"));
  }

  @Test
  void mapsVoidState() {
    assertThat(rows(voided()).head().data().state().code(), is("VOID"));
  }

  @Test
  void restoresDraftState() {
    assertThat(mapping.invoice(withState("DRAFT")).state(), is(new InvoiceState.Draft()));
  }

  @Test
  void restoresIssuedState() {
    assertThat(mapping.invoice(withState("ISSUED")).state(), is(new InvoiceState.Issued()));
  }

  @Test
  void restoresSentState() {
    assertThat(mapping.invoice(withState("SENT")).state(), is(new InvoiceState.Sent()));
  }

  @Test
  void restoresPaidState() {
    assertThat(mapping.invoice(withState("PAID")).state(), is(new InvoiceState.Paid()));
  }

  @Test
  void restoresVoidState() {
    assertThat(mapping.invoice(withState("VOID")).state(), is(new InvoiceState.Void()));
  }

  @Test
  void rejectsUnknownState() {
    assertThrows(IllegalArgumentException.class, () -> mapping.invoice(withState("BAD")));
  }

  @Test
  void rejectsDomainCurrencyMismatch() {
    assertThrows(IllegalArgumentException.class, () -> rows(mixedCurrency()));
  }

  @Test
  void rejectsEmptyLineRows() {
    assertThrows(IllegalArgumentException.class, () -> mapping.invoice(new InvoiceRows(rows(draft()).head(), List.of(), List.of())));
  }

  @Test
  void rejectsMissingTaxRows() {
    assertThrows(IllegalArgumentException.class, () -> mapping.invoice(new InvoiceRows(rows(draft()).head(), rows(draft()).lines(), List.of())));
  }

  @Test
  void rejectsDuplicateTaxRows() {
    assertThrows(IllegalArgumentException.class, () -> mapping.invoice(duplicateTaxRows()));
  }

  @Test
  void rejectsLineCurrencyMismatch() {
    assertThrows(IllegalArgumentException.class, () -> mapping.invoice(withLineCurrency("EUR")));
  }

  @Test
  void rejectsTaxBaseCurrencyMismatch() {
    assertThrows(IllegalArgumentException.class, () -> mapping.invoice(withTaxBaseCurrency("EUR")));
  }

  @Test
  void rejectsTaxAmountCurrencyMismatch() {
    assertThrows(IllegalArgumentException.class, () -> mapping.invoice(withTaxAmountCurrency("EUR")));
  }

  @Test
  void rejectsBlankStoreId() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceStoreKey(" ", 1));
  }

  @Test
  void rejectsNegativeVersion() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceStoreKey("inv-1", -1));
  }

  @Test
  void rejectsBadQuantityDenominator() {
    assertThrows(IllegalArgumentException.class, () -> new QuantityParts(1, 0));
  }

  @Test
  void rejectsBlankLineId() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceLineKey(" ", "inv-1", 1));
  }

  @Test
  void rejectsBlankLineParentId() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceLineKey("line-1", " ", 1));
  }

  @Test
  void rejectsBadLinePosition() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceLineKey("line-1", "inv-1", 0));
  }

  @Test
  void rejectsBlankTaxId() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceTaxKey(" ", "line-1", "LINE"));
  }

  @Test
  void rejectsBlankTaxLineId() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceTaxKey("tax-1", " ", "LINE"));
  }

  @Test
  void rejectsBlankTaxLabel() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceTaxKey("tax-1", "line-1", " "));
  }

  @Test
  void rejectsNegativeTaxRate() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceTaxRow(taxKey(), -1, usd(100), usd(16)));
  }

  private InvoiceRows rows(Invoice invoice) {
    return mapping.rows(meta(), invoice);
  }

  private InvoiceRows withState(String code) {
    var rows = rows(draft());
    var state = new InvoiceStateRow(code, rows.head().data().state().voidMark());
    var data = new InvoiceHeadData(rows.head().data().parties(), rows.head().data().schedule(), state);
    return new InvoiceRows(new InvoiceHeadRow(rows.head().meta(), rows.head().number(), rows.head().currencyCode(), data), rows.lines(), rows.taxes());
  }

  private InvoiceRows withLineCurrency(String currency) {
    var rows = rows(draft());
    var line = rows.lines().getFirst();
    var changed = new InvoiceLineRow(line.key(), line.description(), line.quantity(), new MoneyParts(line.price().amountMinor(), currency));
    return new InvoiceRows(rows.head(), List.of(changed), rows.taxes());
  }

  private InvoiceRows withTaxBaseCurrency(String currency) {
    var rows = rows(draft());
    var tax = rows.taxes().getFirst();
    var changed = new InvoiceTaxRow(tax.key(), tax.rateBps(), new MoneyParts(tax.base().amountMinor(), currency), tax.amount());
    return new InvoiceRows(rows.head(), rows.lines(), List.of(changed));
  }

  private InvoiceRows withTaxAmountCurrency(String currency) {
    var rows = rows(draft());
    var tax = rows.taxes().getFirst();
    var changed = new InvoiceTaxRow(tax.key(), tax.rateBps(), tax.base(), new MoneyParts(tax.amount().amountMinor(), currency));
    return new InvoiceRows(rows.head(), rows.lines(), List.of(changed));
  }

  private InvoiceRows duplicateTaxRows() {
    var rows = rows(draft());
    var tax = rows.taxes().getFirst();
    var extra = new InvoiceTaxRow(new InvoiceTaxKey("tax-extra", tax.key().lineId(), "EXTRA"), tax.rateBps(), tax.base(), tax.amount());
    return new InvoiceRows(rows.head(), rows.lines(), List.of(tax, extra));
  }

  private InvoiceRows shuffledRows() {
    var rows = rows(twoLines());
    return new InvoiceRows(rows.head(), rows.lines().reversed(), rows.taxes());
  }

  private Invoice mixedCurrency() {
    return new Invoice(
      "CORE-00001",
      seller(),
      buyer(),
      issuedOn(),
      terms(),
      List.of(line("USD"), line("EUR")),
      new InvoiceState.Draft()
    );
  }

  private Invoice draft() {
    return new Invoice("CORE-00001", seller(), buyer(), issuedOn(), terms(), List.of(line("USD")), new InvoiceState.Draft());
  }

  private Invoice twoLines() {
    return new Invoice("CORE-00001", seller(), buyer(), issuedOn(), terms(), List.of(line("USD"), secondLine()), new InvoiceState.Draft());
  }

  private Invoice voided() {
    return draft().voidInvoice();
  }

  private LineItem line(String currency) {
    return new LineItem(
      "Consulting",
      new Quantity(new BigDecimal("1.25")),
      new Money(new BigDecimal("12.00"), new CurrencyCode(currency)),
      new Percentage(new BigDecimal("16"))
    );
  }

  private LineItem secondLine() {
    return new LineItem(
      "Support",
      new Quantity(new BigDecimal("2")),
      new Money(new BigDecimal("8.00"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("10"))
    );
  }

  private InvoiceStoreMeta meta() {
    return new InvoiceStoreMeta(new InvoiceStoreKey("inv-1", 7), new StoreClock(now(), now()), Optional.of(voidMark()));
  }

  private VoidMark voidMark() {
    return new VoidMark(now(), "duplicate");
  }

  private Instant now() {
    return Instant.parse("2026-05-24T10:15:30Z");
  }

  private Party seller() {
    return new Party("Seller Ltd", "SELLER-TAX", "seller@example.com");
  }

  private Party buyer() {
    return new Party("Buyer Ltd", "BUYER-TAX", "buyer@example.com");
  }

  private LocalDate issuedOn() {
    return LocalDate.parse("2026-05-24");
  }

  private PaymentTerms terms() {
    return new PaymentTerms(LocalDate.parse("2026-06-24"), "Net 30");
  }

  private MoneyParts usd(long minor) {
    return new MoneyParts(minor, "USD");
  }

  private InvoiceTaxKey taxKey() {
    return new InvoiceTaxKey("tax-1", "line-1", "LINE");
  }
}
