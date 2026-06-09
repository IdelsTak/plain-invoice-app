package com.plaininvoice.invoice.storage;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.nio.file.*;
import java.sql.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

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
  void reopensClosedConnection(@TempDir Path temp) throws Exception {
    try (var store = new LocalStore(temp)) {
      inject(store, proxy(true, false));
      assertThat(store.connect(), not(sameInstance(null)));
    }
  }

  @Test
  void failsWhenCloseFails(@TempDir Path temp) throws Exception {
    var store = new LocalStore(temp);
    inject(store, proxy(false, true));
    assertThrows(IllegalStateException.class, store::close);
  }

  private void inject(LocalStore store, Connection connection) throws Exception {
    var field = LocalStore.class.getDeclaredField("connection");
    field.setAccessible(true);
    field.set(store, connection);
  }

  private Connection proxy(boolean closed, boolean closeFails) {
    return (Connection) Proxy.newProxyInstance(
      getClass().getClassLoader(),
      new Class<?>[] { Connection.class },
      (_, method, _) -> answer(method, closed, closeFails)
    );
  }

  private Object answer(Method method, boolean closed, boolean closeFails) throws Throwable {
    return switch (method.getName()) {
      case "isClosed" -> closed;
      case "close" -> close(closeFails);
      default -> throw new UnsupportedOperationException(method.getName());
    };
  }

  private Object close(boolean fails) throws SQLException {
    if (fails) {
      throw new SQLException("close failed");
    }
    return null;
  }

  private boolean tableExists(LocalStore store) throws Exception {
    try (var stmt = store.connect().prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name='invoices'")) {
      try (var rs = stmt.executeQuery()) {
        return rs.next();
      }
    }
  }
}
