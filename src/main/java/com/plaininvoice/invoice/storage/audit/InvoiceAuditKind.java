package com.plaininvoice.invoice.storage.audit;

public sealed interface InvoiceAuditKind {

  record Created() implements InvoiceAuditKind {}

  record Updated() implements InvoiceAuditKind {}

  record Conflict() implements InvoiceAuditKind {}
}
