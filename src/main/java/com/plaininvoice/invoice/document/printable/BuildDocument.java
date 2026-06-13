package com.plaininvoice.invoice.document.printable;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public final class BuildDocument implements DocumentPort {

  @Override
  public InvoiceDocument document(DocumentRequest request) {
    Objects.requireNonNull(request, "document request cannot be null");
    var invoice = request.invoice();
    return new InvoiceDocument(
      request.meta(),
      header(invoice),
      parties(invoice),
      lines(invoice),
      totals(invoice),
      terms(invoice)
    );
  }

  private DocumentHeader header(Invoice invoice) {
    return new DocumentHeader(invoice.number(), invoice.issuedOn(), invoice.state().getClass().getSimpleName());
  }

  private DocumentParties parties(Invoice invoice) {
    return new DocumentParties(party(invoice.seller()), party(invoice.buyer()));
  }

  private DocumentParty party(Party party) {
    return new DocumentParty(party.name(), party.taxId(), party.email());
  }

  private List<DocumentLine> lines(Invoice invoice) {
    var lines = new ArrayList<DocumentLine>();
    for (var i = 0; i < invoice.lineItems().size(); i++) {
      lines.add(line(i + 1, invoice.lineItems().get(i)));
    }
    return lines;
  }

  private DocumentLine line(int position, LineItem item) {
    return new DocumentLine(
      position,
      item.description(),
      item.quantity(),
      item.taxRate(),
      new DocumentAmounts(item.unitPrice(), item.subtotal(), item.tax().tax(), item.total())
    );
  }

  private DocumentTotals totals(Invoice invoice) {
    return new DocumentTotals(invoice.subtotal(), invoice.totalTax(), invoice.totalDue());
  }

  private DocumentTerms terms(Invoice invoice) {
    return new DocumentTerms(invoice.paymentTerms().dueDate(), invoice.paymentTerms().note());
  }
}
