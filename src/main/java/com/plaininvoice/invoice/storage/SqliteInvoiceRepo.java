package com.plaininvoice.invoice.storage;

import com.plaininvoice.invoice.lifecycle.*;
import java.sql.*;
import java.time.*;
import java.util.*;

public final class SqliteInvoiceRepo implements InvoiceRepository {
  private final Connection connection;
  private final InvoiceMapping mapping;

  public SqliteInvoiceRepo(Connection connection) {
    this(connection, new InvoiceMapping());
  }

  SqliteInvoiceRepo(Connection connection, InvoiceMapping mapping) {
    this.connection = Objects.requireNonNull(connection, "connection cannot be null");
    this.mapping = Objects.requireNonNull(mapping, "invoice mapping cannot be null");
    foreignKeys();
    new SqliteSchemaV1().bootstrap(connection);
  }

  @Override
  public StoredInvoice save(StoredInvoice invoice) {
    Objects.requireNonNull(invoice, "stored invoice cannot be null");
    var next = next(invoice.meta());
    var rows = mapping.rows(next, invoice.invoice());
    begin();
    try {
      if (invoice.meta().key().version() == 0) {
        insertHead(rows.head());
      } else {
        updateHead(rows.head(), invoice.meta().key().version());
        deleteLines(rows.head().meta().key().id());
      }
      insertLines(rows.lines());
      insertTaxes(rows.taxes());
      commit();
      return new StoredInvoice(next, invoice.invoice());
    } catch (RuntimeException ex) {
      rollback();
      throw ex;
    }
  }

  @Override
  public Optional<StoredInvoice> load(String id) {
    Objects.requireNonNull(id, "invoice id cannot be null");
    var head = readHead(id);
    return head.map(row -> new StoredInvoice(row.meta(), mapping.invoice(new InvoiceRows(row, readLines(id), readTaxes(id)))));
  }

  @Override
  public List<StoredInvoice> list() {
    var invoices = new ArrayList<StoredInvoice>();
    try (
      var stmt = connection.prepareStatement(
        "SELECT id FROM invoices ORDER BY issued_on DESC, number DESC"
      );
      var rs = stmt.executeQuery()
    ) {
      while (rs.next()) {
        invoices.add(load(rs.getString("id")).orElseThrow());
      }
      return List.copyOf(invoices);
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice list failed", ex);
    }
  }

  private InvoiceStoreMeta next(InvoiceStoreMeta meta) {
    var version = meta.key().version() == 0 ? 1 : meta.key().version() + 1;
    return new InvoiceStoreMeta(new InvoiceStoreKey(meta.key().id(), version), meta.clock(), meta.voidMark());
  }

  private void foreignKeys() {
    try (var stmt = connection.createStatement()) {
      stmt.execute("PRAGMA foreign_keys = ON");
    } catch (SQLException ex) {
      throw new IllegalStateException("foreign keys setup failed", ex);
    }
  }

  private void begin() {
    try (var stmt = connection.createStatement()) {
      stmt.execute("BEGIN IMMEDIATE");
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice transaction begin failed", ex);
    }
  }

  private void commit() {
    try (var stmt = connection.createStatement()) {
      stmt.execute("COMMIT");
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice transaction commit failed", ex);
    }
  }

  private void rollback() {
    try (var stmt = connection.createStatement()) {
      stmt.execute("ROLLBACK");
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice transaction rollback failed", ex);
    }
  }

  private void insertHead(InvoiceHeadRow row) {
    try (
      var stmt = connection.prepareStatement(
        """
        INSERT INTO invoices(
          id, number, currency_code, seller_name, seller_tax_id, seller_email,
          buyer_name, buyer_tax_id, buyer_email, issued_on, due_date, payment_note,
          state, voided_at, voided_reason, version, created_at, updated_at
        ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
      )
    ) {
      setHead(stmt, row);
      stmt.executeUpdate();
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice insert failed", ex);
    }
  }

  private void updateHead(InvoiceHeadRow row, long expected) {
    try (
      var stmt = connection.prepareStatement(
        """
        UPDATE invoices SET
          number=?, currency_code=?, seller_name=?, seller_tax_id=?, seller_email=?,
          buyer_name=?, buyer_tax_id=?, buyer_email=?, issued_on=?, due_date=?,
          payment_note=?, state=?, voided_at=?, voided_reason=?, version=?,
          created_at=?, updated_at=?
        WHERE id=? AND version=?
        """
      )
    ) {
      setHeadUpdate(stmt, row, expected);
      var changed = stmt.executeUpdate();
      if (changed == 0) {
        throw new StoreConflict("invoice version conflict");
      }
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice update failed", ex);
    }
  }

  private void setHead(PreparedStatement stmt, InvoiceHeadRow row) throws SQLException {
    stmt.setString(1, row.meta().key().id());
    stmt.setString(2, row.number());
    stmt.setString(3, row.currencyCode());
    setParties(stmt, row, 4);
    setSchedule(stmt, row, 10);
    setState(stmt, row, 13);
    stmt.setLong(16, row.meta().key().version());
    stmt.setString(17, text(row.meta().clock().createdAt()));
    stmt.setString(18, text(row.meta().clock().updatedAt()));
  }

  private void setHeadUpdate(PreparedStatement stmt, InvoiceHeadRow row, long expected) throws SQLException {
    stmt.setString(1, row.number());
    stmt.setString(2, row.currencyCode());
    setParties(stmt, row, 3);
    setSchedule(stmt, row, 9);
    setState(stmt, row, 12);
    stmt.setLong(15, row.meta().key().version());
    stmt.setString(16, text(row.meta().clock().createdAt()));
    stmt.setString(17, text(row.meta().clock().updatedAt()));
    stmt.setString(18, row.meta().key().id());
    stmt.setLong(19, expected);
  }

  private void setParties(PreparedStatement stmt, InvoiceHeadRow row, int start) throws SQLException {
    var parties = row.data().parties();
    stmt.setString(start, parties.seller().name());
    stmt.setString(start + 1, parties.seller().taxId());
    stmt.setString(start + 2, parties.seller().email());
    stmt.setString(start + 3, parties.buyer().name());
    stmt.setString(start + 4, parties.buyer().taxId());
    stmt.setString(start + 5, parties.buyer().email());
  }

  private void setSchedule(PreparedStatement stmt, InvoiceHeadRow row, int start) throws SQLException {
    var schedule = row.data().schedule();
    stmt.setString(start, schedule.issuedOn().toString());
    stmt.setString(start + 1, schedule.paymentTerms().dueDate().toString());
    stmt.setString(start + 2, schedule.paymentTerms().note());
  }

  private void setState(PreparedStatement stmt, InvoiceHeadRow row, int start) throws SQLException {
    var state = row.data().state();
    stmt.setString(start, state.code());
    stmt.setString(start + 1, state.voidMark().map(mark -> text(mark.voidedAt())).orElse(null));
    stmt.setString(start + 2, state.voidMark().map(VoidMark::reason).orElse(null));
  }

  private void deleteLines(String invoiceId) {
    try (var stmt = connection.prepareStatement("DELETE FROM invoice_lines WHERE invoice_id=?")) {
      stmt.setString(1, invoiceId);
      stmt.executeUpdate();
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice line delete failed", ex);
    }
  }

  private void insertLines(List<InvoiceLineRow> rows) {
    for (var row : rows) {
      insertLine(row);
    }
  }

  private void insertLine(InvoiceLineRow row) {
    try (
      var stmt = connection.prepareStatement(
        """
        INSERT INTO invoice_lines(
          id, invoice_id, position, description, quantity_numerator,
          quantity_denominator, unit_amount_minor, currency_code
        ) VALUES(?, ?, ?, ?, ?, ?, ?, ?)
        """
      )
    ) {
      stmt.setString(1, row.key().id());
      stmt.setString(2, row.key().invoiceId());
      stmt.setInt(3, row.key().position());
      stmt.setString(4, row.description());
      stmt.setLong(5, row.quantity().numerator());
      stmt.setLong(6, row.quantity().denominator());
      stmt.setLong(7, row.price().amountMinor());
      stmt.setString(8, row.price().currencyCode());
      stmt.executeUpdate();
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice line insert failed", ex);
    }
  }

  private void insertTaxes(List<InvoiceTaxRow> rows) {
    for (var row : rows) {
      insertTax(row);
    }
  }

  private void insertTax(InvoiceTaxRow row) {
    try (
      var stmt = connection.prepareStatement(
        """
        INSERT INTO invoice_taxes(
          id, invoice_line_id, tax_label, tax_rate_bps,
          tax_base_minor, tax_amount_minor, currency_code
        ) VALUES(?, ?, ?, ?, ?, ?, ?)
        """
      )
    ) {
      stmt.setString(1, row.key().id());
      stmt.setString(2, row.key().lineId());
      stmt.setString(3, row.key().label());
      stmt.setLong(4, row.rateBps());
      stmt.setLong(5, row.base().amountMinor());
      stmt.setLong(6, row.amount().amountMinor());
      stmt.setString(7, row.amount().currencyCode());
      stmt.executeUpdate();
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice tax insert failed", ex);
    }
  }

  private Optional<InvoiceHeadRow> readHead(String id) {
    try {
      var stmt = connection.prepareStatement(
        """
        SELECT * FROM invoices WHERE id=?
        """
      );
      stmt.setString(1, id);
      var rs = stmt.executeQuery();
      if (!rs.next()) {
        return Optional.empty();
      }
      return Optional.of(head(rs));
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice load failed", ex);
    }
  }

  private InvoiceHeadRow head(ResultSet rs) throws SQLException {
    var meta = meta(rs);
    var parties = new InvoicePartiesRow(
      new Party(rs.getString("seller_name"), rs.getString("seller_tax_id"), rs.getString("seller_email")),
      new Party(rs.getString("buyer_name"), rs.getString("buyer_tax_id"), rs.getString("buyer_email"))
    );
    var schedule = new InvoiceScheduleRow(
      LocalDate.parse(rs.getString("issued_on")),
      new PaymentTerms(LocalDate.parse(rs.getString("due_date")), rs.getString("payment_note"))
    );
    var state = new InvoiceStateRow(rs.getString("state"), meta.voidMark());
    return new InvoiceHeadRow(meta, rs.getString("number"), rs.getString("currency_code"), new InvoiceHeadData(parties, schedule, state));
  }

  private InvoiceStoreMeta meta(ResultSet rs) throws SQLException {
    var key = new InvoiceStoreKey(rs.getString("id"), rs.getLong("version"));
    var clock = new StoreClock(Instant.parse(rs.getString("created_at")), Instant.parse(rs.getString("updated_at")));
    return new InvoiceStoreMeta(key, clock, voidMark(rs));
  }

  private Optional<VoidMark> voidMark(ResultSet rs) throws SQLException {
    var voidedAt = rs.getString("voided_at");
    if (voidedAt == null) {
      return Optional.empty();
    }
    return Optional.of(new VoidMark(Instant.parse(voidedAt), rs.getString("voided_reason")));
  }

  private List<InvoiceLineRow> readLines(String invoiceId) {
    var rows = new ArrayList<InvoiceLineRow>();
    try (
      var stmt = connection.prepareStatement(
        "SELECT * FROM invoice_lines WHERE invoice_id=? ORDER BY position"
      )
    ) {
      stmt.setString(1, invoiceId);
      try (var rs = stmt.executeQuery()) {
        while (rs.next()) {
          rows.add(line(rs));
        }
      }
      return List.copyOf(rows);
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice line load failed", ex);
    }
  }

  private InvoiceLineRow line(ResultSet rs) throws SQLException {
    return new InvoiceLineRow(
      new InvoiceLineKey(rs.getString("id"), rs.getString("invoice_id"), rs.getInt("position")),
      rs.getString("description"),
      new QuantityParts(rs.getLong("quantity_numerator"), rs.getLong("quantity_denominator")),
      new MoneyParts(rs.getLong("unit_amount_minor"), rs.getString("currency_code"))
    );
  }

  private List<InvoiceTaxRow> readTaxes(String invoiceId) {
    var rows = new ArrayList<InvoiceTaxRow>();
    try (
      var stmt = connection.prepareStatement(
        """
        SELECT t.* FROM invoice_taxes t
        JOIN invoice_lines l ON l.id=t.invoice_line_id
        WHERE l.invoice_id=?
        ORDER BY l.position, t.tax_label
        """
      )
    ) {
      stmt.setString(1, invoiceId);
      try (var rs = stmt.executeQuery()) {
        while (rs.next()) {
          rows.add(tax(rs));
        }
      }
      return List.copyOf(rows);
    } catch (SQLException ex) {
      throw new IllegalStateException("invoice tax load failed", ex);
    }
  }

  private InvoiceTaxRow tax(ResultSet rs) throws SQLException {
    var currency = rs.getString("currency_code");
    return new InvoiceTaxRow(
      new InvoiceTaxKey(rs.getString("id"), rs.getString("invoice_line_id"), rs.getString("tax_label")),
      rs.getLong("tax_rate_bps"),
      new MoneyParts(rs.getLong("tax_base_minor"), currency),
      new MoneyParts(rs.getLong("tax_amount_minor"), currency)
    );
  }

  private String text(Instant instant) {
    return instant.toString();
  }
}
