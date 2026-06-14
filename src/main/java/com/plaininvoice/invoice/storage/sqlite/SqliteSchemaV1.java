package com.plaininvoice.invoice.storage.sqlite;

import java.nio.charset.*;
import java.sql.*;
import java.util.*;

public final class SqliteSchemaV1 {
  private static final String SCRIPT = "/db/migration/V1__init.sql";
  private final String script;

  public SqliteSchemaV1() {
    this(SCRIPT);
  }

  SqliteSchemaV1(String script) {
    this.script = Objects.requireNonNull(script, "script path cannot be null");
  }

  public void bootstrap(Connection connection) {
    Objects.requireNonNull(connection, "connection cannot be null");
    var sql = loadScript();
    var statements = split(sql);
    try {
      for (var statement : statements) {
        try (var stmt = connection.createStatement()) {
          stmt.execute(statement);
        }
      }
    } catch (SQLException ex) {
      throw new IllegalStateException("schema bootstrap failed", ex);
    }
  }

  private String loadScript() {
    var stream = SqliteSchemaV1.class.getResourceAsStream(script);
    if (stream == null) {
      throw new IllegalStateException("schema script not found");
    }
    String sql;
      try (var scanner = new Scanner(stream, StandardCharsets.UTF_8).useDelimiter("\\A")) {
          sql = scanner.hasNext() ? scanner.next() : "";
      }
    return sql;
  }

  private List<String> split(String sql) {
    var statements = new ArrayList<String>();
    var current = new StringBuilder();
    var trigger = false;
    for (var fragment : sql.split(";")) {
      var statement = fragment.trim();
      if (!statement.isEmpty()) {
        if (!current.isEmpty()) {
          current.append(';');
        }
        current.append(statement);
        if (statement.toUpperCase(Locale.ROOT).startsWith("CREATE TRIGGER")) {
          trigger = true;
        }
        if (trigger && "END".equalsIgnoreCase(statement)) {
          trigger = false;
        }
        if (!trigger) {
          statements.add(current.toString());
          current.setLength(0);
        }
      }
    }
    return statements;
  }
}
