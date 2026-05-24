package com.plaininvoice.invoice.lifecycle;

public sealed interface InvoiceState {

  record Draft() implements InvoiceState {}

  record Issued() implements InvoiceState {}

  record Sent() implements InvoiceState {}

  record Paid() implements InvoiceState {}

  record Void() implements InvoiceState {}
}
