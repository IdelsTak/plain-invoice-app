package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

final class LayoutSamples {

  private LayoutSamples() {}

  static InvoiceDocument document() {
    var port = new BuildDocument();
    return port.document(new DocumentRequest(DocumentSamples.invoice(), DocumentSamples.meta()));
  }

  static LayoutPage page() {
    return new LayoutPage("A4", "portrait", margins());
  }

  static PageMargins margins() {
    return new PageMargins(36, 36, 36, 36);
  }

  static HeaderToken header() {
    return new HeaderToken("Invoice", DocumentSamples.header().number(), DocumentSamples.header().issuedOn(), "Draft");
  }

  static PartyToken party(String role) {
    return new PartyToken(role, DocumentSamples.party(role));
  }

  static PartyBlock parties() {
    return new PartyBlock(party("seller"), party("buyer"));
  }

  static LineToken line() {
    var line = DocumentSamples.lines().getFirst();
    return new LineToken(
      line.position(),
      line.description(),
      line.quantity(),
      line.taxRate(),
      line.amounts(),
      breakHint()
    );
  }

  static LineTableToken lines() {
    return new LineTableToken(List.of(line()));
  }

  static TotalsToken totals() {
    var totals = DocumentSamples.totals();
    return new TotalsToken(totals.subtotal(), totals.tax(), totals.totalDue());
  }

  static TermsToken terms() {
    return new TermsToken(DocumentSamples.terms().dueDate(), "Net 30");
  }

  static FooterToken footer() {
    return new FooterToken("Invoice INV-1000");
  }

  static BreakHint breakHint() {
    return new BreakHint("auto", "avoid", "auto");
  }

  static PageHints hints() {
    return new PageHints("running", "running", breakHint());
  }
}
