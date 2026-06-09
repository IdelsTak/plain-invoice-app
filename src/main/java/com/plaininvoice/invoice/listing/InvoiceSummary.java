package com.plaininvoice.invoice.listing;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import com.plaininvoice.invoice.storage.*;
import java.time.*;
import java.util.*;

public record InvoiceSummary(
  String id,
  long version,
  String number,
  String buyerName,
  LocalDate issuedOn,
  LocalDate dueDate,
  String state,
  Money totalDue
) {
  public InvoiceSummary {
    Objects.requireNonNull(id, "invoice id cannot be null");
    Objects.requireNonNull(number, "invoice number cannot be null");
    Objects.requireNonNull(buyerName, "buyer name cannot be null");
    Objects.requireNonNull(issuedOn, "issue date cannot be null");
    Objects.requireNonNull(dueDate, "due date cannot be null");
    Objects.requireNonNull(state, "invoice state cannot be null");
    Objects.requireNonNull(totalDue, "invoice total due cannot be null");
    id = id.trim();
    number = number.trim();
    buyerName = buyerName.trim();
    state = state.trim();
    if (id.isEmpty()) {
      throw new IllegalArgumentException("invoice id cannot be blank");
    }
  }

  static InvoiceSummary from(StoredInvoice stored) {
    Objects.requireNonNull(stored, "stored invoice cannot be null");
    var invoice = stored.invoice();
    return new InvoiceSummary(
      stored.meta().key().id(),
      stored.meta().key().version(),
      invoice.number(),
      invoice.buyer().name(),
      invoice.issuedOn(),
      invoice.paymentTerms().dueDate(),
      state(invoice.state()),
      invoice.totalDue()
    );
  }

  private static String state(InvoiceState state) {
    return switch (state) {
      case InvoiceState.Draft _ -> "DRAFT";
      case InvoiceState.Issued _ -> "ISSUED";
      case InvoiceState.Sent _ -> "SENT";
      case InvoiceState.Paid _ -> "PAID";
      case InvoiceState.Void _ -> "VOID";
    };
  }
}
