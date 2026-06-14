package com.plaininvoice.invoice.storage.local;

import com.plaininvoice.invoice.storage.backup.*;
import com.plaininvoice.invoice.storage.sqlite.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class LocalStoreTest {

  @Test
  void rejectsNullHome() {
    assertThrows(NullPointerException.class, () -> new LocalStore((StoreHome) null));
  }

  @Test
  void exposesDatabasePath(@TempDir Path temp) {
    assertThat(new LocalStore(temp).database(), is(temp.resolve("plain-invoice.sqlite")));
  }

  @Test
  void createsDirectory(@TempDir Path temp) {
    var directory = temp.resolve("nested");
    try (var store = new LocalStore(directory)) {
      store.connect();
      assertThat(Files.isDirectory(directory), is(true));
    }
  }

  @Test
  void opensDatabaseFile(@TempDir Path temp) {
    try (var store = new LocalStore(temp)) {
      store.connect();
      assertThat(Files.isRegularFile(store.database()), is(true));
    }
  }

  @Test
  void appliesForeignKeysAtStartup(@TempDir Path temp) throws Exception {
    try (var store = new LocalStore(temp); var stmt = store.connect().createStatement(); var rs = stmt.executeQuery("PRAGMA foreign_keys")) {
      assertThat(rs.next() && rs.getInt(1) == 1, is(true));
    }
  }

  @Test
  void appliesDeleteJournalModeAtStartup(@TempDir Path temp) throws Exception {
    try (var store = new LocalStore(temp); var stmt = store.connect().createStatement(); var rs = stmt.executeQuery("PRAGMA journal_mode")) {
      assertThat(rs.next() && "delete".equalsIgnoreCase(rs.getString(1)), is(true));
    }
  }

  @Test
  void appliesBusyTimeoutAtStartup(@TempDir Path temp) throws Exception {
    try (var store = new LocalStore(temp); var stmt = store.connect().createStatement(); var rs = stmt.executeQuery("PRAGMA busy_timeout")) {
      assertThat(rs.next() && rs.getInt(1) == store.busyTimeOut(), is(true));
    }
  }

  @Test
  void reusesOpenConnection(@TempDir Path temp) {
    try (var store = new LocalStore(temp)) {
      var connection = store.connect();
      assertThat(store.connect(), sameInstance(connection));
    }
  }

  @Test
  void opensAfterClose(@TempDir Path temp) {
    try (var store = new LocalStore(temp)) {
      var connection = store.connect();
      store.close();
      assertThat(store.connect(), not(sameInstance(connection)));
    }
  }

  @Test
  void exposesInvoiceRepo(@TempDir Path temp) {
    try (var store = new LocalStore(temp)) {
      assertThat(store.invoices(), instanceOf(SqliteInvoiceRepo.class));
    }
  }

  @Test
  void createsBackup(@TempDir Path temp) {
    try (var store = new LocalStore(temp.resolve("store"))) {
      store.invoices();
      assertThat(Files.isRegularFile(store.backup(temp.resolve("backups"), now()).path()), is(true));
    }
  }

  @Test
  void restoresBackup(@TempDir Path temp) throws Exception {
    var archive = archive(temp.resolve("source"));
    try (var store = new LocalStore(temp.resolve("target"))) {
      store.restore(archive.path(), false);
      assertThat(sampleValue(store.database()), is("saved"));
    }
  }

  @Test
  void dryRunRestoreKeepsExistingStore(@TempDir Path temp) throws Exception {
    var archive = archive(temp.resolve("source"));
    writeSample(temp.resolve("target").resolve("plain-invoice.sqlite"), "stale");
    try (var store = new LocalStore(temp.resolve("target"))) {
      store.restore(archive.path(), true);
      assertThat(sampleValue(store.database()), is("stale"));
    }
  }

  @Test
  void bootstrapsSchema(@TempDir Path temp) throws Exception {
    try (var store = new LocalStore(temp)) {
      store.invoices();
      assertThat(tableExists(store), is(true));
    }
  }

  @Test
  void closeBeforeOpenIsSafe(@TempDir Path temp) {
    try (var store = new LocalStore(temp)) {
      store.close();
      assertThat(store.database(), is(temp.resolve("plain-invoice.sqlite")));
    }
  }

  @Test
  void failsWhenDirectoryIsFile(@TempDir Path temp) throws Exception {
    var file = Files.createFile(temp.resolve("not-directory"));
    try (var store = new LocalStore(file)) {
      assertThrows(IllegalStateException.class, store::connect);
    }
  }

  @Test
  void failsWhenDatabaseIsDirectory(@TempDir Path temp) throws Exception {
    Files.createDirectory(temp.resolve("store.sqlite"));
    try (var store = new LocalStore(new StoreHome(temp, "store.sqlite"))) {
      assertThrows(IllegalStateException.class, store::connect);
    }
  }

  @Test
  void rejectsCorruptDatabaseAtStartup(@TempDir Path temp) throws Exception {
    Files.writeString(temp.resolve("plain-invoice.sqlite"), "not a database");
    try (var store = new LocalStore(temp)) {
      assertThrows(IllegalStateException.class, store::connect);
    }
  }

  @Test
  void rejectsNullConfig() {
    assertThrows(NullPointerException.class, () -> new LocalStore(new StoreHome(Path.of("data")), null));
  }

  @Test
  void acceptsExplicitConfig(@TempDir Path temp) {
    try (var store = new LocalStore(new StoreHome(temp), new SqliteConnectionConfig())) {
      assertThat(store.database(), is(temp.resolve("plain-invoice.sqlite")));
    }
  }

  @Test
  void reopensClosedConnection(@TempDir Path temp) throws Exception {
    try (var store = new LocalStore(new StoreHome(temp), new SqliteConnectionConfig(), new FakeConnPort(true, false))) {
      assertThat(store.connect(), not(sameInstance(null)));
    }
  }

  @Test
  void reopensWhenCachedConnCloses(@TempDir Path temp) throws Exception {
    var port = new FlipConnPort();
    try (var store = new LocalStore(new StoreHome(temp), new SqliteConnectionConfig(), port)) {
      store.connect();
      store.connect();
      assertThat(port.opens(), is(2));
    }
  }

  @Test
  void failsWhenCloseFails(@TempDir Path temp) throws Exception {
    var store = new LocalStore(new StoreHome(temp), new SqliteConnectionConfig(), new FakeConnPort(false, true));
    store.connect();
    assertThrows(IllegalStateException.class, store::close);
  }

  private Instant now() {
    return Instant.parse("2026-05-24T10:15:30Z");
  }

  private StoreBackupArchive archive(Path temp) throws Exception {
    var home = new StoreHome(temp.resolve("store"));
    Files.createDirectories(home.directory());
    writeSample(home.database(), "saved");
    return new CreateStoreBackup().execute(new StoreBackupRequest(home, temp.resolve("backups"), now()));
  }

  private void writeSample(Path database, String value) throws Exception {
    Files.createDirectories(database.getParent());
    try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database.toAbsolutePath())) {
      try (var stmt = connection.createStatement()) {
        stmt.execute("CREATE TABLE sample(id INTEGER PRIMARY KEY, name TEXT NOT NULL)");
        stmt.execute("INSERT INTO sample(name) VALUES('" + value + "')");
      }
    }
  }

  private String sampleValue(Path database) throws Exception {
    try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database.toAbsolutePath())) {
      try (var stmt = connection.prepareStatement("SELECT name FROM sample")) {
        try (var rs = stmt.executeQuery()) {
          rs.next();
          return rs.getString(1);
        }
      }
    }
  }

  private boolean tableExists(LocalStore store) throws Exception {
    try (var stmt = store.connect().prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name='invoices'")) {
      try (var rs = stmt.executeQuery()) {
        return rs.next();
      }
    }
  }

  private static final class FakeConnPort implements StoreConnPort {
    private final boolean closed;
    private final boolean closeFails;
    private final Connection token;

    private FakeConnPort(boolean closed, boolean closeFails) throws SQLException {
      this.closed = closed;
      this.closeFails = closeFails;
      this.token = DriverManager.getConnection("jdbc:sqlite::memory:");
    }

    @Override
    public Connection open(Path database, SqliteConnectionConfig config) {
      return token;
    }

    @Override
    public boolean isClosed(Connection connection) {
      return closed;
    }

    @Override
    public void close(Connection connection) throws SQLException {
      if (closeFails) {
        throw new SQLException("close failed");
      }
      token.close();
    }
  }

  private static final class FlipConnPort implements StoreConnPort {
    private final Connection token;
    private int opens;
    private boolean first = true;

    private FlipConnPort() throws SQLException {
      this.token = DriverManager.getConnection("jdbc:sqlite::memory:");
    }

    @Override
    public Connection open(Path database, SqliteConnectionConfig config) {
      opens++;
      return token;
    }

    @Override
    public boolean isClosed(Connection connection) {
      if (first) {
        first = false;
        return true;
      }
      return false;
    }

    @Override
    public void close(Connection connection) throws SQLException {
      token.close();
    }

    private int opens() {
      return opens;
    }
  }
}
