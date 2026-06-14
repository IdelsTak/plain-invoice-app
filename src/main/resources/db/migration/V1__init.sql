CREATE TABLE IF NOT EXISTS invoices (
  id TEXT PRIMARY KEY,
  number TEXT NOT NULL UNIQUE,
  currency_code TEXT NOT NULL,
  seller_name TEXT NOT NULL,
  seller_tax_id TEXT,
  seller_email TEXT,
  buyer_name TEXT NOT NULL,
  buyer_tax_id TEXT,
  buyer_email TEXT,
  issued_on TEXT NOT NULL CHECK (issued_on GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'),
  due_date TEXT NOT NULL CHECK (due_date GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'),
  payment_note TEXT,
  state TEXT NOT NULL CHECK (state IN ('DRAFT','ISSUED','SENT','PAID','VOID')),
  voided_at TEXT,
  voided_reason TEXT,
  version INTEGER NOT NULL,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL,
  CHECK ((state <> 'VOID') OR (voided_at IS NOT NULL))
);

CREATE TABLE IF NOT EXISTS invoice_lines (
  id TEXT PRIMARY KEY,
  invoice_id TEXT NOT NULL,
  position INTEGER NOT NULL,
  description TEXT NOT NULL,
  quantity_numerator INTEGER NOT NULL,
  quantity_denominator INTEGER NOT NULL CHECK (quantity_denominator > 0),
  unit_amount_minor INTEGER NOT NULL CHECK (unit_amount_minor >= 0),
  currency_code TEXT NOT NULL,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
  UNIQUE (invoice_id, position)
);

CREATE TABLE IF NOT EXISTS invoice_taxes (
  id TEXT PRIMARY KEY,
  invoice_line_id TEXT NOT NULL,
  tax_label TEXT NOT NULL,
  tax_rate_bps INTEGER NOT NULL CHECK (tax_rate_bps >= 0),
  tax_base_minor INTEGER NOT NULL,
  tax_amount_minor INTEGER NOT NULL,
  currency_code TEXT NOT NULL,
  FOREIGN KEY (invoice_line_id) REFERENCES invoice_lines(id) ON DELETE CASCADE,
  UNIQUE (invoice_line_id, tax_label)
);

CREATE TABLE IF NOT EXISTS invoice_audit_events (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  invoice_id TEXT NOT NULL,
  event_type TEXT NOT NULL CHECK (event_type IN ('CREATED','UPDATED','CONFLICT')),
  invoice_version INTEGER NOT NULL CHECK (invoice_version >= 0),
  occurred_at TEXT NOT NULL,
  detail TEXT NOT NULL,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE RESTRICT
);

CREATE TRIGGER IF NOT EXISTS trg_invoice_audit_no_update
BEFORE UPDATE ON invoice_audit_events
BEGIN
  SELECT RAISE(ABORT, 'invoice audit is append-only');
END;

CREATE TRIGGER IF NOT EXISTS trg_invoice_audit_no_delete
BEFORE DELETE ON invoice_audit_events
BEGIN
  SELECT RAISE(ABORT, 'invoice audit is append-only');
END;

CREATE TRIGGER IF NOT EXISTS trg_invoice_lines_currency_insert
BEFORE INSERT ON invoice_lines
WHEN NEW.currency_code <> (SELECT currency_code FROM invoices WHERE id = NEW.invoice_id)
BEGIN
  SELECT RAISE(ABORT, 'invoice line currency must match invoice currency');
END;

CREATE TRIGGER IF NOT EXISTS trg_invoice_lines_currency_update
BEFORE UPDATE OF currency_code, invoice_id ON invoice_lines
WHEN NEW.currency_code <> (SELECT currency_code FROM invoices WHERE id = NEW.invoice_id)
BEGIN
  SELECT RAISE(ABORT, 'invoice line currency must match invoice currency');
END;

CREATE TRIGGER IF NOT EXISTS trg_invoice_taxes_currency_insert
BEFORE INSERT ON invoice_taxes
WHEN NEW.currency_code <> (SELECT currency_code FROM invoice_lines WHERE id = NEW.invoice_line_id)
BEGIN
  SELECT RAISE(ABORT, 'invoice tax currency must match line currency');
END;

CREATE TRIGGER IF NOT EXISTS trg_invoice_taxes_currency_update
BEFORE UPDATE OF currency_code, invoice_line_id ON invoice_taxes
WHEN NEW.currency_code <> (SELECT currency_code FROM invoice_lines WHERE id = NEW.invoice_line_id)
BEGIN
  SELECT RAISE(ABORT, 'invoice tax currency must match line currency');
END;

CREATE INDEX IF NOT EXISTS idx_invoices_state ON invoices(state);
CREATE INDEX IF NOT EXISTS idx_invoices_issued_on ON invoices(issued_on);
CREATE INDEX IF NOT EXISTS idx_invoices_unpaid_due ON invoices(due_date) WHERE state NOT IN ('PAID','VOID');
CREATE INDEX IF NOT EXISTS idx_invoice_lines_invoice_id ON invoice_lines(invoice_id);
CREATE INDEX IF NOT EXISTS idx_invoice_taxes_line_id ON invoice_taxes(invoice_line_id);
CREATE INDEX IF NOT EXISTS idx_invoice_audit_invoice_id ON invoice_audit_events(invoice_id);
