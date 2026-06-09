package com.plaininvoice.invoice.storage;

import java.sql.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class SqliteSchemaV1Test {

  @Test
  void createsInvoicesTable() throws Exception {
    try (var connection = open()) {
      new SqliteSchemaV1().bootstrap(connection);
      assertThat(tableExists(connection, "invoices"), is(true));
    }
  }

  @Test
  void createsInvoiceLinesTable() throws Exception {
    try (var connection = open()) {
      new SqliteSchemaV1().bootstrap(connection);
      assertThat(tableExists(connection, "invoice_lines"), is(true));
    }
  }

  @Test
  void createsInvoiceTaxesTable() throws Exception {
    try (var connection = open()) {
      new SqliteSchemaV1().bootstrap(connection);
      assertThat(tableExists(connection, "invoice_taxes"), is(true));
    }
  }

  @Test
  void createsInvoiceAuditTable() throws Exception {
    try (var connection = open()) {
      new SqliteSchemaV1().bootstrap(connection);
      assertThat(tableExists(connection, "invoice_audit_events"), is(true));
    }
  }

  @Test
  void rejectsNullConnection() {
    assertThrows(NullPointerException.class, () -> new SqliteSchemaV1().bootstrap(null));
  }

  @Test
  void rejectsNullScriptPath() {
    assertThrows(NullPointerException.class, () -> new SqliteSchemaV1(null));
  }

  @Test
  void failsWhenScriptResourceMissing() throws Exception {
    try (var connection = open()) {
      assertThrows(IllegalStateException.class, () -> new SqliteSchemaV1("/db/migration/missing.sql").bootstrap(connection));
    }
  }

  @Test
  void failsWhenScriptContainsInvalidSql() throws Exception {
    try (var connection = open()) {
      assertThrows(IllegalStateException.class, () -> new SqliteSchemaV1("/db/migration/V1__bad.sql").bootstrap(connection));
    }
  }

  @Test
  void handlesEmptyScript() throws Exception {
    try (var connection = open()) {
      new SqliteSchemaV1("/db/migration/V1__empty.sql").bootstrap(connection);
      assertThat(tableExists(connection, "invoices"), is(false));
    }
  }

  @Test
  void enforcesInvoiceNumberUnique() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      insertInvoice(connection, "inv-1", "INV-0001", "DRAFT");
      assertThrows(SQLException.class, () -> insertInvoice(connection, "inv-2", "INV-0001", "DRAFT"));
    }
  }

  @Test
  void enforcesStateConstraint() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      assertThrows(SQLException.class, () -> insertInvoice(connection, "inv-1", "INV-0001", "ARCHIVED"));
    }
  }

  @Test
  void enforcesDateFormatConstraint() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      assertThrows(SQLException.class, () -> insertInvoiceWithDate(connection, "inv-1", "INV-0001", "2026/05/24", "2026-06-24"));
    }
  }

  @Test
  void enforcesVoidTimestampConstraint() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      assertThrows(SQLException.class, () -> insertInvoice(connection, "inv-1", "INV-0001", "VOID"));
    }
  }

  @Test
  void enforcesLineForeignKey() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      assertThrows(SQLException.class, () -> insertLine(connection, "line-1", "missing", 1));
    }
  }

  @Test
  void enforcesLinePositionUniquePerInvoice() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      insertInvoice(connection, "inv-1", "INV-0001", "DRAFT");
      insertLine(connection, "line-1", "inv-1", 1);
      assertThrows(SQLException.class, () -> insertLine(connection, "line-2", "inv-1", 1));
    }
  }

  @Test
  void computesLineTotalMinor() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      insertInvoice(connection, "inv-1", "INV-0001", "DRAFT");
      insertLine(connection, "line-1", "inv-1", 1);
      assertThat(lineTotal(connection, "line-1"), is(500L));
    }
  }

  @Test
  void enforcesTaxForeignKey() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      assertThrows(SQLException.class, () -> insertTax(connection, "tax-1", "missing", "VAT"));
    }
  }

  @Test
  void enforcesTaxLabelUniquePerLine() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      insertInvoice(connection, "inv-1", "INV-0001", "DRAFT");
      insertLine(connection, "line-1", "inv-1", 1);
      insertTax(connection, "tax-1", "line-1", "VAT");
      assertThrows(SQLException.class, () -> insertTax(connection, "tax-2", "line-1", "VAT"));
    }
  }

  @Test
  void enforcesAuditForeignKey() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      assertThrows(SQLException.class, () -> insertAudit(connection, "missing", "CREATED"));
    }
  }

  @Test
  void enforcesAuditTypeConstraint() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      insertInvoice(connection, "inv-1", "INV-0001", "DRAFT");
      assertThrows(SQLException.class, () -> insertAudit(connection, "inv-1", "ARCHIVED"));
    }
  }

  @Test
  void preventsAuditUpdate() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      insertInvoice(connection, "inv-1", "INV-0001", "DRAFT");
      insertAudit(connection, "inv-1", "CREATED");
      assertThrows(SQLException.class, () -> updateAudit(connection));
    }
  }

  @Test
  void preventsAuditDelete() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      insertInvoice(connection, "inv-1", "INV-0001", "DRAFT");
      insertAudit(connection, "inv-1", "CREATED");
      assertThrows(SQLException.class, () -> deleteAudit(connection));
    }
  }

  @Test
  void appliesSchemaIdempotently() throws Exception {
    try (var connection = open()) {
      var schema = new SqliteSchemaV1();
      schema.bootstrap(connection);
      schema.bootstrap(connection);
      assertThat(tableExists(connection, "invoices"), is(true));
    }
  }

  private Connection open() throws Exception {
    return DriverManager.getConnection("jdbc:sqlite::memory:");
  }

  private boolean tableExists(Connection connection, String table) throws Exception {
    try (var stmt = connection.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name=?")) {
      stmt.setString(1, table);
      try (var rs = stmt.executeQuery()) {
        return rs.next();
      }
    }
  }

  private void insertInvoice(Connection connection, String id, String number, String state) throws Exception {
    insertInvoiceWithDate(connection, id, number, "2026-05-24", "2026-06-24", state, null);
  }

  private void insertInvoiceWithDate(Connection connection, String id, String number, String issuedOn, String dueDate) throws Exception {
    insertInvoiceWithDate(connection, id, number, issuedOn, dueDate, "DRAFT", null);
  }

  private void insertInvoiceWithDate(
    Connection connection,
    String id,
    String number,
    String issuedOn,
    String dueDate,
    String state,
    String voidedAt
  ) throws Exception {
    try (
      var stmt = connection.prepareStatement(
        """
        INSERT INTO invoices(
          id, number, currency_code, seller_name, seller_tax_id, seller_email,
          buyer_name, buyer_tax_id, buyer_email, issued_on, due_date, payment_note,
          state, voided_at, voided_reason, version, created_at, updated_at
        ) VALUES(?, ?, 'USD', 'Seller Ltd', 'TAX-01', 'seller@example.com',
          'Buyer LLC', 'TAX-02', 'buyer@example.com', ?, ?, 'Net 30',
          ?, ?, 'Manual void', 1, '2026-05-24T00:00:00Z', '2026-05-24T00:00:00Z')
        """
      )
    ) {
      stmt.setString(1, id);
      stmt.setString(2, number);
      stmt.setString(3, issuedOn);
      stmt.setString(4, dueDate);
      stmt.setString(5, state);
      stmt.setString(6, voidedAt);
      stmt.executeUpdate();
    }
  }

  private void insertLine(Connection connection, String id, String invoiceId, int position) throws Exception {
    try (
      var stmt = connection.prepareStatement(
        """
        INSERT INTO invoice_lines(
          id, invoice_id, position, description, quantity_numerator, quantity_denominator,
          unit_amount_minor, currency_code
        ) VALUES(?, ?, ?, 'Service', 2, 1, 250, 'USD')
        """
      )
    ) {
      stmt.setString(1, id);
      stmt.setString(2, invoiceId);
      stmt.setInt(3, position);
      stmt.executeUpdate();
    }
  }

  private long lineTotal(Connection connection, String id) throws Exception {
    try (var stmt = connection.prepareStatement("SELECT line_total_minor FROM invoice_lines WHERE id=?")) {
      stmt.setString(1, id);
      try (var rs = stmt.executeQuery()) {
        rs.next();
        return rs.getLong(1);
      }
    }
  }

  private void insertTax(Connection connection, String id, String lineId, String label) throws Exception {
    try (
      var stmt = connection.prepareStatement(
        """
        INSERT INTO invoice_taxes(
          id, invoice_line_id, tax_label, tax_rate_bps, tax_base_minor, tax_amount_minor, currency_code
        ) VALUES(?, ?, ?, 1600, 500, 80, 'USD')
        """
      )
    ) {
      stmt.setString(1, id);
      stmt.setString(2, lineId);
      stmt.setString(3, label);
      stmt.executeUpdate();
    }
  }

  private void insertAudit(Connection connection, String invoiceId, String type) throws Exception {
    try (
      var stmt = connection.prepareStatement(
        """
        INSERT INTO invoice_audit_events(invoice_id, event_type, invoice_version, occurred_at, detail)
        VALUES(?, ?, 1, '2026-05-24T00:00:00Z', 'test')
        """
      )
    ) {
      stmt.setString(1, invoiceId);
      stmt.setString(2, type);
      stmt.executeUpdate();
    }
  }

  private void updateAudit(Connection connection) throws Exception {
    try (var stmt = connection.prepareStatement("UPDATE invoice_audit_events SET detail='changed'")) {
      stmt.executeUpdate();
    }
  }

  private void deleteAudit(Connection connection) throws Exception {
    try (var stmt = connection.prepareStatement("DELETE FROM invoice_audit_events")) {
      stmt.executeUpdate();
    }
  }
}
