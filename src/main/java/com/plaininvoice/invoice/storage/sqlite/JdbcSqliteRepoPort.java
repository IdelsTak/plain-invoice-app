package com.plaininvoice.invoice.storage.sqlite;

import java.sql.*;
import java.util.*;

final class JdbcSqliteRepoPort implements SqliteRepoJdbcPort {
  private final Connection connection;

  JdbcSqliteRepoPort(Connection connection) {
    this.connection = Objects.requireNonNull(connection, "connection cannot be null");
  }

  @Override
  public void execute(String sql) throws SQLException {
    try (var stmt = connection.createStatement()) {
      stmt.execute(sql);
    }
  }

  @Override
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return connection.prepareStatement(sql);
  }
}
