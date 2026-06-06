package com.plaininvoice.invoice.storage;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;

public final class InvoiceMapping {
  private static final String TAX_LABEL = "LINE";

  public InvoiceRows rows(InvoiceStoreMeta meta, Invoice invoice) {
    Objects.requireNonNull(meta, "invoice storage metadata cannot be null");
    Objects.requireNonNull(invoice, "invoice cannot be null");
    var currency = currency(invoice);
    var head = new InvoiceHeadRow(meta, invoice.number(), currency.value(), headData(meta, invoice));
    var lines = lineRows(meta.key().id(), invoice.lineItems());
    var taxes = taxRows(lines, invoice.lineItems());
    return new InvoiceRows(head, lines, taxes);
  }

  public Invoice invoice(InvoiceRows rows) {
    Objects.requireNonNull(rows, "invoice rows cannot be null");
    if (rows.lines().isEmpty()) {
      throw new IllegalArgumentException("invoice rows must include at least one line");
    }
    var head = rows.head();
    var lineItems = lineItems(head, rows.lines(), rows.taxes());
    return new Invoice(
      head.number(),
      head.data().parties().seller(),
      head.data().parties().buyer(),
      head.data().schedule().issuedOn(),
      head.data().schedule().paymentTerms(),
      lineItems,
      state(head.data().state().code())
    );
  }

  private InvoiceHeadData headData(InvoiceStoreMeta meta, Invoice invoice) {
    return new InvoiceHeadData(
      new InvoicePartiesRow(invoice.seller(), invoice.buyer()),
      new InvoiceScheduleRow(invoice.issuedOn(), invoice.paymentTerms()),
      new InvoiceStateRow(stateCode(invoice.state()), meta.voidMark())
    );
  }

  private List<InvoiceLineRow> lineRows(String invoiceId, List<LineItem> items) {
    var rows = new ArrayList<InvoiceLineRow>();
    for (var i = 0; i < items.size(); i++) {
      var position = i + 1;
      var item = items.get(i);
      var key = new InvoiceLineKey(invoiceId + "-line-" + position, invoiceId, position);
      rows.add(new InvoiceLineRow(key, item.description(), quantityParts(item.quantity()), moneyParts(item.unitPrice())));
    }
    return rows;
  }

  private List<InvoiceTaxRow> taxRows(List<InvoiceLineRow> rows, List<LineItem> items) {
    var taxes = new ArrayList<InvoiceTaxRow>();
    for (var i = 0; i < rows.size(); i++) {
      var line = rows.get(i);
      var tax = items.get(i).tax();
      var key = new InvoiceTaxKey(line.key().id() + "-tax", line.key().id(), TAX_LABEL);
      taxes.add(new InvoiceTaxRow(key, bps(tax.rate()), moneyParts(tax.base()), moneyParts(tax.tax())));
    }
    return taxes;
  }

  private List<LineItem> lineItems(InvoiceHeadRow head, List<InvoiceLineRow> lines, List<InvoiceTaxRow> taxes) {
    var byLine = taxes.stream().collect(Collectors.groupingBy(tax -> tax.key().lineId()));
    if (byLine.size() != lines.size()) {
      throw new IllegalArgumentException("each invoice line must have one tax row");
    }
    var items = new ArrayList<LineItem>();
    for (var line : ordered(lines)) {
      var taxRows = byLine.getOrDefault(line.key().id(), List.of());
      if (taxRows.size() != 1) {
        throw new IllegalArgumentException("each invoice line must have one tax row");
      }
      requireCurrency(head.currencyCode(), line.price());
      var tax = taxRows.getFirst();
      requireCurrency(head.currencyCode(), tax.base());
      requireCurrency(head.currencyCode(), tax.amount());
      items.add(new LineItem(line.description(), quantity(line.quantity()), money(line.price()), percentage(tax.rateBps())));
    }
    return items;
  }

  private List<InvoiceLineRow> ordered(List<InvoiceLineRow> lines) {
    return lines.stream().sorted(Comparator.comparingInt(line -> line.key().position())).toList();
  }

  private CurrencyCode currency(Invoice invoice) {
    var currency = invoice.lineItems().getFirst().unitPrice().currencyCode();
    for (var line : invoice.lineItems()) {
      if (!currency.equals(line.unitPrice().currencyCode())) {
        throw new IllegalArgumentException("invoice line currency must match invoice currency");
      }
    }
    return currency;
  }

  private void requireCurrency(String currencyCode, MoneyParts parts) {
    if (!currencyCode.equals(parts.currencyCode())) {
      throw new IllegalArgumentException("row currency must match invoice currency");
    }
  }

  private MoneyParts moneyParts(Money money) {
    var scale = money.currencyCode().currency().getDefaultFractionDigits();
    var minor = money.amount().movePointRight(scale).longValueExact();
    return new MoneyParts(minor, money.currencyCode().value());
  }

  private Money money(MoneyParts parts) {
    var currency = new CurrencyCode(parts.currencyCode());
    var scale = currency.currency().getDefaultFractionDigits();
    return new Money(BigDecimal.valueOf(parts.amountMinor(), scale), currency);
  }

  private QuantityParts quantityParts(Quantity quantity) {
    var value = quantity.value();
    var scale = Math.max(0, value.scale());
    var scaled = value.setScale(scale);
    var denominator = BigInteger.TEN.pow(scale);
    return new QuantityParts(scaled.unscaledValue().longValueExact(), denominator.longValueExact());
  }

  private Quantity quantity(QuantityParts parts) {
    return new Quantity(BigDecimal.valueOf(parts.numerator()).divide(BigDecimal.valueOf(parts.denominator())));
  }

  private long bps(Percentage percentage) {
    return percentage.value().movePointRight(2).longValueExact();
  }

  private Percentage percentage(long bps) {
    return new Percentage(BigDecimal.valueOf(bps).movePointLeft(2));
  }

  private String stateCode(InvoiceState state) {
    return switch (state) {
      case InvoiceState.Draft _ -> "DRAFT";
      case InvoiceState.Issued _ -> "ISSUED";
      case InvoiceState.Sent _ -> "SENT";
      case InvoiceState.Paid _ -> "PAID";
      case InvoiceState.Void _ -> "VOID";
    };
  }

  private InvoiceState state(String code) {
    return switch (code) {
      case "DRAFT" -> new InvoiceState.Draft();
      case "ISSUED" -> new InvoiceState.Issued();
      case "SENT" -> new InvoiceState.Sent();
      case "PAID" -> new InvoiceState.Paid();
      case "VOID" -> new InvoiceState.Void();
      default -> throw new IllegalArgumentException("unknown invoice state");
    };
  }
}
