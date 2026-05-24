package com.plaininvoice.invoice.lifecycle;

import com.plaininvoice.invoice.pricing.*;
import java.time.*;
import java.util.*;

public record Invoice(
  String number,
  Party seller,
  Party buyer,
  LocalDate issuedOn,
  PaymentTerms paymentTerms,
  List<LineItem> lineItems,
  InvoiceState state
) {
  public Invoice {
    Objects.requireNonNull(number, "invoice number cannot be null");
    Objects.requireNonNull(seller, "seller cannot be null");
    Objects.requireNonNull(buyer, "buyer cannot be null");
    Objects.requireNonNull(issuedOn, "issue date cannot be null");
    Objects.requireNonNull(paymentTerms, "payment terms cannot be null");
    Objects.requireNonNull(lineItems, "line items cannot be null");
    Objects.requireNonNull(state, "invoice state cannot be null");

    number = number.trim();
    if (number.isEmpty()) {
      throw new IllegalArgumentException("invoice number cannot be blank");
    }
    if (lineItems.isEmpty()) {
      throw new IllegalArgumentException("invoice must contain at least one line item");
    }
    lineItems = List.copyOf(lineItems);

    if (paymentTerms.dueDate().isBefore(issuedOn)) {
      throw new IllegalArgumentException("payment due date cannot be before issue date");
    }
  }

  public Money subtotal() {
    Money seed = lineItems.getFirst().subtotal();
    for (int i = 1; i < lineItems.size(); i++) {
      seed = seed.add(lineItems.get(i).subtotal());
    }
    return seed;
  }

  public Money totalTax() {
    Money seed = lineItems.getFirst().tax().tax();
    for (int i = 1; i < lineItems.size(); i++) {
      seed = seed.add(lineItems.get(i).tax().tax());
    }
    return seed;
  }

  public Money totalDue() {
    return subtotal().add(totalTax());
  }

  public Invoice issue() {
    if (state != InvoiceState.DRAFT) {
      throw new IllegalStateException("only draft invoices can be issued");
    }
    return new Invoice(number, seller, buyer, issuedOn, paymentTerms, lineItems, InvoiceState.ISSUED);
  }

  public Invoice markSent() {
    if (state != InvoiceState.ISSUED) {
      throw new IllegalStateException("only issued invoices can be marked sent");
    }
    return new Invoice(number, seller, buyer, issuedOn, paymentTerms, lineItems, InvoiceState.SENT);
  }

  public Invoice markPaid() {
    if (state != InvoiceState.ISSUED && state != InvoiceState.SENT) {
      throw new IllegalStateException("only issued or sent invoices can be marked paid");
    }
    return new Invoice(number, seller, buyer, issuedOn, paymentTerms, lineItems, InvoiceState.PAID);
  }

  public Invoice voidInvoice() {
    if (state == InvoiceState.PAID) {
      throw new IllegalStateException("paid invoices cannot be voided");
    }
    return new Invoice(number, seller, buyer, issuedOn, paymentTerms, lineItems, InvoiceState.VOID);
  }
}
