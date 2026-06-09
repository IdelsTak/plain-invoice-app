package com.plaininvoice.invoice.storage;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import java.lang.reflect.*;
import java.math.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

final class SqliteInvoiceRepoTest {

  @Test
  void savesNewInvoiceAtVersionOne() throws Exception {
    try (var connection = open()) {
      var saved = repo(connection).save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThat(saved.meta().key().version(), is(1L));
    }
  }

  @Test
  void writesCreateAuditEvent() throws Exception {
    try (var connection = open()) {
      repo(connection).save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThat(auditType(connection, "inv-1", 1), is("CREATED"));
    }
  }

  @Test
  void writesCreateAuditVersion() throws Exception {
    try (var connection = open()) {
      repo(connection).save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThat(auditVersion(connection, "inv-1", 1), is(1L));
    }
  }

  @Test
  void loadsSavedInvoiceNumber() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThat(repo.load("inv-1").orElseThrow().invoice().number(), is("CORE-00001"));
    }
  }

  @Test
  void loadsSavedInvoiceLines() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThat(repo.load("inv-1").orElseThrow().invoice().lineItems().size(), is(1));
    }
  }

  @Test
  void loadsSavedVersion() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThat(repo.load("inv-1").orElseThrow().meta().key().version(), is(1L));
    }
  }

  @Test
  void loadsMissingInvoiceEmpty() throws Exception {
    try (var connection = open()) {
      assertThat(repo(connection).load("missing"), is(Optional.empty()));
    }
  }

  @Test
  void listsInvoices() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", LocalDate.parse("2026-05-24"), money("12.00"))));
      assertThat(repo.list().size(), is(1));
    }
  }

  @Test
  void listsRecentInvoiceFirst() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", LocalDate.parse("2026-05-24"), money("12.00"))));
      repo.save(stored("inv-2", 0, invoice("CORE-00002", LocalDate.parse("2026-05-25"), money("8.00"))));
      assertThat(repo.list().getFirst().invoice().number(), is("CORE-00002"));
    }
  }

  @Test
  void listsNumberDescendingWithinDate() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(stored("inv-2", 0, invoice("CORE-00002", issuedOn(), money("8.00"))));
      assertThat(repo.list().getFirst().invoice().number(), is("CORE-00002"));
    }
  }

  @Test
  void updatesInvoiceVersion() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var saved = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      var updated = repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("15.00"))));
      assertThat(updated.meta().key().version(), is(2L));
    }
  }

  @Test
  void appendsUpdateAuditEvent() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var saved = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("15.00"))));
      assertThat(auditType(connection, "inv-1", 2), is("UPDATED"));
    }
  }

  @Test
  void keepsCreateAuditAfterUpdate() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var saved = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("15.00"))));
      assertThat(auditCount(connection, "inv-1"), is(2));
    }
  }

  @Test
  void updatesInvoiceAmount() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var saved = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("15.00"))));
      assertThat(repo.load("inv-1").orElseThrow().invoice().subtotal().amount(), comparesEqualTo(new BigDecimal("15.00")));
    }
  }

  @Test
  void rejectsStaleVersion() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var saved = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("15.00"))));
      assertThrows(StoreConflict.class, () -> repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("16.00")))));
    }
  }

  @Test
  void appendsConflictAuditEvent() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var saved = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("15.00"))));
      stale(repo, saved);
      assertThat(auditType(connection, "inv-1", 3), is("CONFLICT"));
    }
  }

  @Test
  void recordsConflictReason() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var saved = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("15.00"))));
      stale(repo, saved);
      assertThat(auditDetail(connection, "inv-1", 3), is("invoice version conflict"));
    }
  }

  @Test
  void rejectsDuplicateCreate() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThrows(IllegalStateException.class, () -> repo.save(stored("inv-2", 0, invoice("CORE-00001", issuedOn(), money("8.00")))));
    }
  }

  @Test
  void rollsBackFailedCreate() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      assertThrows(IllegalStateException.class, () -> repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("-1.00")))));
    }
  }

  @Test
  void failsWhenForeignKeysFail() throws Exception {
    try (var connection = open()) {
      assertThrows(IllegalStateException.class, () -> repo(failing(connection, "PRAGMA foreign_keys")));
    }
  }

  @Test
  void failsWhenBeginFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(failing(connection, "BEGIN IMMEDIATE"));
      assertThrows(IllegalStateException.class, () -> repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00")))));
    }
  }

  @Test
  void failsWhenCommitFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(failing(connection, "COMMIT"));
      assertThrows(IllegalStateException.class, () -> repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00")))));
    }
  }

  @Test
  void failsWhenRollbackFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(failing(connection, "INSERT INTO invoice_lines", "ROLLBACK"));
      assertThrows(IllegalStateException.class, () -> repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00")))));
    }
  }

  @Test
  void failsWhenDeleteFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(failing(connection, "DELETE FROM invoice_lines"));
      var saved = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThrows(IllegalStateException.class, () -> repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("15.00")))));
    }
  }

  @Test
  void failsWhenTaxInsertFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(failing(connection, "INSERT INTO invoice_taxes"));
      assertThrows(IllegalStateException.class, () -> repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00")))));
    }
  }

  @Test
  void failsWhenConflictAuditFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var saved = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("15.00"))));
      var failing = repo(failing(connection, "INSERT INTO invoice_audit_events"));
      assertThrows(IllegalStateException.class, () -> failing.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("16.00")))));
    }
  }

  @Test
  void failsWhenListFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(failing(connection, "SELECT id FROM invoices"));
      assertThrows(IllegalStateException.class, repo::list);
    }
  }

  @Test
  void failsWhenLoadFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(failing(connection, "SELECT * FROM invoices"));
      assertThrows(IllegalStateException.class, () -> repo.load("inv-1"));
    }
  }

  @Test
  void failsWhenLineLoadFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThrows(IllegalStateException.class, () -> repo(failing(connection, "SELECT * FROM invoice_lines")).load("inv-1"));
    }
  }

  @Test
  void failsWhenTaxLoadFails() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      assertThrows(IllegalStateException.class, () -> repo(failing(connection, "SELECT t.* FROM invoice_taxes")).load("inv-1"));
    }
  }

  @Test
  void rejectsHeadWithoutLines() throws Exception {
    try (var connection = open()) {
      repo(connection);
      insertHeadOnly(connection);
      assertThrows(IllegalArgumentException.class, () -> repo(connection).load("inv-1"));
    }
  }

  @Test
  void failedCreateLeavesNoHead() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      assertThrows(IllegalStateException.class, () -> repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("-1.00")))));
      assertThat(repo.load("inv-1"), is(Optional.empty()));
    }
  }

  @Test
  void failedCreateLeavesNoAudit() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      failedCreate(repo);
      assertThat(auditCount(connection, "inv-1"), is(0));
    }
  }

  @Test
  void rollsBackFailedUpdate() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var first = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(stored("inv-2", 0, invoice("CORE-00002", issuedOn(), money("8.00"))));
      assertThrows(IllegalStateException.class, () -> repo.save(new StoredInvoice(first.meta(), invoice("CORE-00002", issuedOn(), money("15.00")))));
    }
  }

  @Test
  void failedUpdateKeepsOriginal() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      var first = repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00"))));
      repo.save(stored("inv-2", 0, invoice("CORE-00002", issuedOn(), money("8.00"))));
      assertThrows(IllegalStateException.class, () -> repo.save(new StoredInvoice(first.meta(), invoice("CORE-00002", issuedOn(), money("15.00")))));
      assertThat(repo.load("inv-1").orElseThrow().invoice().number(), is("CORE-00001"));
    }
  }

  @Test
  void savesVoidState() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00")).voidInvoice()));
      assertThat(repo.load("inv-1").orElseThrow().invoice().state(), is(new InvoiceState.Void()));
    }
  }

  @Test
  void savesVoidReason() throws Exception {
    try (var connection = open()) {
      var repo = repo(connection);
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("12.00")).voidInvoice()));
      assertThat(voidReason(connection, "inv-1"), is("duplicate"));
    }
  }

  @Test
  void rejectsNullConnection() {
    assertThrows(NullPointerException.class, () -> new SqliteInvoiceRepo(null));
  }

  @Test
  void rejectsNullMapping() throws Exception {
    try (var connection = open()) {
      assertThrows(NullPointerException.class, () -> new SqliteInvoiceRepo(connection, null));
    }
  }

  @Test
  void rejectsNullSave() throws Exception {
    try (var connection = open()) {
      assertThrows(NullPointerException.class, () -> repo(connection).save(null));
    }
  }

  @Test
  void rejectsNullLoadId() throws Exception {
    try (var connection = open()) {
      assertThrows(NullPointerException.class, () -> repo(connection).load(null));
    }
  }

  @Test
  void listsEmptyStore() throws Exception {
    try (var connection = open()) {
      assertThat(repo(connection).list(), is(List.of()));
    }
  }

  private SqliteInvoiceRepo repo(Connection connection) {
    return new SqliteInvoiceRepo(connection);
  }

  private Connection open() throws Exception {
    return DriverManager.getConnection("jdbc:sqlite::memory:");
  }

  private Connection failing(Connection connection, String... tokens) {
    return (Connection) Proxy.newProxyInstance(
      Connection.class.getClassLoader(),
      new Class<?>[] {Connection.class},
      (_, method, args) -> connectionCall(connection, method, args, tokens)
    );
  }

  private Object connectionCall(Connection connection, Method method, Object[] args, String[] tokens) throws Throwable {
    if ("createStatement".equals(method.getName())) {
      return statement((Statement) invoke(method, connection, args), tokens);
    }
    if ("prepareStatement".equals(method.getName()) && matches((String) args[0], tokens)) {
      throw new SQLException("forced sql failure");
    }
    return invoke(method, connection, args);
  }

  private Statement statement(Statement statement, String[] tokens) {
    return (Statement) Proxy.newProxyInstance(
      Statement.class.getClassLoader(),
      new Class<?>[] {Statement.class},
      (_, method, args) -> statementCall(statement, method, args, tokens)
    );
  }

  private Object statementCall(Statement statement, Method method, Object[] args, String[] tokens) throws Throwable {
    if ("execute".equals(method.getName()) && matches((String) args[0], tokens)) {
      throw new SQLException("forced sql failure");
    }
    return invoke(method, statement, args);
  }

  private Object invoke(Method method, Object target, Object[] args) throws Throwable {
    try {
      return method.invoke(target, args);
    } catch (InvocationTargetException ex) {
      throw ex.getCause();
    }
  }

  private boolean matches(String sql, String[] tokens) {
    for (var token : tokens) {
      if (sql.contains(token)) {
        return true;
      }
    }
    return false;
  }

  private StoredInvoice stored(String id, long version, Invoice invoice) {
    return new StoredInvoice(meta(id, version), invoice);
  }

  private void stale(SqliteInvoiceRepo repo, StoredInvoice saved) {
    try {
      repo.save(new StoredInvoice(saved.meta(), invoice("CORE-00001", issuedOn(), money("16.00"))));
    } catch (StoreConflict _) {
      return;
    }
    throw new AssertionError("stale save should conflict");
  }

  private void failedCreate(SqliteInvoiceRepo repo) {
    try {
      repo.save(stored("inv-1", 0, invoice("CORE-00001", issuedOn(), money("-1.00"))));
    } catch (IllegalStateException _) {
      return;
    }
    throw new AssertionError("invalid create should fail");
  }

  private InvoiceStoreMeta meta(String id, long version) {
    return new InvoiceStoreMeta(new InvoiceStoreKey(id, version), new StoreClock(now(), now()), Optional.of(voidMark()));
  }

  private VoidMark voidMark() {
    return new VoidMark(now(), "duplicate");
  }

  private Instant now() {
    return Instant.parse("2026-05-24T10:15:30Z");
  }

  private Invoice invoice(String number, LocalDate issuedOn, Money unitPrice) {
    return new Invoice(number, seller(), buyer(), issuedOn, terms(issuedOn), List.of(line(unitPrice)), new InvoiceState.Draft());
  }

  private LineItem line(Money unitPrice) {
    return new LineItem(
      "Consulting",
      new Quantity(new BigDecimal("1")),
      unitPrice,
      new Percentage(new BigDecimal("16"))
    );
  }

  private Money money(String amount) {
    return new Money(new BigDecimal(amount), new CurrencyCode("USD"));
  }

  private Party seller() {
    return new Party("Seller Ltd", "SELLER-TAX", "seller@example.com");
  }

  private Party buyer() {
    return new Party("Buyer Ltd", "BUYER-TAX", "buyer@example.com");
  }

  private LocalDate issuedOn() {
    return LocalDate.parse("2026-05-24");
  }

  private PaymentTerms terms(LocalDate issuedOn) {
    return new PaymentTerms(issuedOn.plusDays(30), "Net 30");
  }

  private String voidReason(Connection connection, String id) throws Exception {
    try (var stmt = connection.prepareStatement("SELECT voided_reason FROM invoices WHERE id=?")) {
      stmt.setString(1, id);
      try (var rs = stmt.executeQuery()) {
        rs.next();
        return rs.getString(1);
      }
    }
  }

  private int auditCount(Connection connection, String id) throws Exception {
    try (var stmt = connection.prepareStatement("SELECT COUNT(*) FROM invoice_audit_events WHERE invoice_id=?")) {
      stmt.setString(1, id);
      try (var rs = stmt.executeQuery()) {
        rs.next();
        return rs.getInt(1);
      }
    }
  }

  private String auditType(Connection connection, String id, int sequence) throws Exception {
    return auditText(connection, id, sequence, "event_type");
  }

  private String auditDetail(Connection connection, String id, int sequence) throws Exception {
    return auditText(connection, id, sequence, "detail");
  }

  private long auditVersion(Connection connection, String id, int sequence) throws Exception {
    try (
      var stmt = connection.prepareStatement(
        "SELECT invoice_version FROM invoice_audit_events WHERE invoice_id=? ORDER BY id LIMIT 1 OFFSET ?"
      )
    ) {
      stmt.setString(1, id);
      stmt.setInt(2, sequence - 1);
      try (var rs = stmt.executeQuery()) {
        rs.next();
        return rs.getLong(1);
      }
    }
  }

  private String auditText(Connection connection, String id, int sequence, String column) throws Exception {
    try (
      var stmt = connection.prepareStatement(
        "SELECT " + column + " FROM invoice_audit_events WHERE invoice_id=? ORDER BY id LIMIT 1 OFFSET ?"
      )
    ) {
      stmt.setString(1, id);
      stmt.setInt(2, sequence - 1);
      try (var rs = stmt.executeQuery()) {
        rs.next();
        return rs.getString(1);
      }
    }
  }

  private void insertHeadOnly(Connection connection) throws Exception {
    try (
      var stmt = connection.prepareStatement(
        """
        INSERT INTO invoices(
          id, number, currency_code, seller_name, seller_tax_id, seller_email,
          buyer_name, buyer_tax_id, buyer_email, issued_on, due_date, payment_note,
          state, voided_at, voided_reason, version, created_at, updated_at
        ) VALUES('inv-1', 'CORE-00001', 'USD', 'Seller Ltd', 'SELLER-TAX', 'seller@example.com',
          'Buyer Ltd', 'BUYER-TAX', 'buyer@example.com', '2026-05-24', '2026-06-24', 'Net 30',
          'DRAFT', NULL, NULL, 1, '2026-05-24T10:15:30Z', '2026-05-24T10:15:30Z')
        """
      )
    ) {
      stmt.executeUpdate();
    }
  }
}
