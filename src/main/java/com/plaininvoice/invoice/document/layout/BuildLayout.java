package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public final class BuildLayout implements LayoutPort {

  @Override
  public LayoutDocument layout(InvoiceDocument document) {
    Objects.requireNonNull(document, "invoice document cannot be null");
    return new LayoutDocument(
      document.meta(),
      page(),
      header(document),
      parties(document),
      lines(document),
      totals(document),
      terms(document),
      footer(document),
      hints()
    );
  }

  private LayoutPage page() {
    return new LayoutPage("A4", "portrait", new PageMargins(36, 36, 36, 36));
  }

  private HeaderToken header(InvoiceDocument document) {
    var header = document.header();
    return new HeaderToken(document.meta().title(), header.number(), header.issuedOn(), header.state());
  }

  private PartyBlock parties(InvoiceDocument document) {
    return new PartyBlock(
      new PartyToken("seller", document.parties().seller()),
      new PartyToken("buyer", document.parties().buyer())
    );
  }

  private LineTableToken lines(InvoiceDocument document) {
    var lines = new ArrayList<LineToken>();
    for (var line : document.lines()) {
      lines.add(line(line));
    }
    return new LineTableToken(lines);
  }

  private LineToken line(DocumentLine line) {
    return new LineToken(
      line.position(),
      line.description(),
      line.quantity(),
      line.taxRate(),
      line.amounts(),
      new BreakHint("auto", "avoid", "auto")
    );
  }

  private TotalsToken totals(InvoiceDocument document) {
    var totals = document.totals();
    return new TotalsToken(totals.subtotal(), totals.tax(), totals.totalDue());
  }

  private TermsToken terms(InvoiceDocument document) {
    var terms = document.terms();
    return new TermsToken(terms.dueDate(), terms.note());
  }

  private FooterToken footer(InvoiceDocument document) {
    return new FooterToken(document.meta().title() + " " + document.header().number());
  }

  private PageHints hints() {
    return new PageHints("running", "running", new BreakHint("auto", "avoid", "auto"));
  }
}
