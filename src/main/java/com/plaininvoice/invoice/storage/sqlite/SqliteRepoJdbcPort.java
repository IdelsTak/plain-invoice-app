package com.plaininvoice.invoice.storage.sqlite;

import java.sql.*;

interface SqliteRepoJdbcPort {
  void execute(String sql) throws SQLException;

  PreparedStatement prepareStatement(String sql) throws SQLException;
}
