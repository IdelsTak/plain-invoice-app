package com.plaininvoice.invoice.storage.sqlite;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import org.junit.jupiter.api.*;

final class JdbcSqlitePragmaPortTest {

  @Test
  void executesPragma() throws Exception {
    try (var connection = open()) {
      new JdbcSqlitePragmaPort(connection).execute("PRAGMA foreign_keys = ON", "foreign keys setup failed");
      try (var stmt = connection.createStatement(); var rs = stmt.executeQuery("PRAGMA foreign_keys")) {
        assertThat(rs.next() && rs.getInt(1) == 1, is(true));
      }
    }
  }

  @Test
  void failsWhenExecuteThrowsSqlException() throws Exception {
    try (var connection = open()) {
      assertThrows(IllegalStateException.class, () ->
        new JdbcSqlitePragmaPort(connection).execute("THIS IS NOT SQL", "pragma execute failed"));
    }
  }

  @Test
  void readsIntegerPragma() throws Exception {
    try (var connection = open()) {
      try (var stmt = connection.createStatement()) {
        stmt.execute("PRAGMA foreign_keys = ON");
      }
      assertThat(new JdbcSqlitePragmaPort(connection).queryInt("PRAGMA foreign_keys", "foreign keys read failed"), is(1));
    }
  }

  @Test
  void failsWhenIntegerQueryReturnsNoRow() throws Exception {
    try (var connection = open()) {
      assertThrows(IllegalStateException.class, () ->
        new JdbcSqlitePragmaPort(connection).queryInt("SELECT 1 WHERE 1 = 0", "integer read failed"));
    }
  }

  @Test
  void failsWhenIntegerQueryThrowsSqlException() throws Exception {
    try (var connection = open()) {
      assertThrows(IllegalStateException.class, () ->
        new JdbcSqlitePragmaPort(connection).queryInt("SELECT nope", "integer read failed"));
    }
  }

  @Test
  void readsTextPragma() throws Exception {
    try (var connection = open()) {
      assertThat(new JdbcSqlitePragmaPort(connection).queryText("PRAGMA journal_mode", "journal mode read failed"), is("memory"));
    }
  }

  @Test
  void failsWhenTextQueryReturnsNoRow() throws Exception {
    try (var connection = open()) {
      assertThrows(IllegalStateException.class, () ->
        new JdbcSqlitePragmaPort(connection).queryText("SELECT 'ok' WHERE 1 = 0", "text read failed"));
    }
  }

  @Test
  void failsWhenTextQueryThrowsSqlException() throws Exception {
    try (var connection = open()) {
      assertThrows(IllegalStateException.class, () ->
        new JdbcSqlitePragmaPort(connection).queryText("SELECT nope", "text read failed"));
    }
  }

  private Connection open() throws Exception {
    return DriverManager.getConnection("jdbc:sqlite::memory:");
  }
}
