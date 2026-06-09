package com.plaininvoice.invoice.storage;

public sealed interface InvoiceAuditKind {

  record Created() implements InvoiceAuditKind {}

  record Updated() implements InvoiceAuditKind {}

  record Conflict() implements InvoiceAuditKind {}
}
