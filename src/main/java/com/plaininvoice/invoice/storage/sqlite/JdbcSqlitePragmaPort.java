package com.plaininvoice.invoice.storage.sqlite;

import java.sql.*;

final class JdbcSqlitePragmaPort implements SqlitePragmaPort {
  private final Connection connection;

  JdbcSqlitePragmaPort(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void execute(String sql, String message) {
    try (var stmt = connection.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException ex) {
      throw new IllegalStateException(message, ex);
    }
  }

  @Override
  public int queryInt(String sql, String message) {
    try (var stmt = connection.createStatement(); var rs = stmt.executeQuery(sql)) {
      if (!rs.next()) {
        throw new IllegalStateException(message);
      }
      return rs.getInt(1);
    } catch (SQLException ex) {
      throw new IllegalStateException(message, ex);
    }
  }

  @Override
  public String queryText(String sql, String message) {
    try (var stmt = connection.createStatement(); var rs = stmt.executeQuery(sql)) {
      if (!rs.next()) {
        throw new IllegalStateException(message);
      }
      return rs.getString(1);
    } catch (SQLException ex) {
      throw new IllegalStateException(message, ex);
    }
  }
}
