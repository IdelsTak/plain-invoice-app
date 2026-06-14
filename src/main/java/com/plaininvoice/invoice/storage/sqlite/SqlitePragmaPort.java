package com.plaininvoice.invoice.storage.sqlite;

interface SqlitePragmaPort {
  void execute(String sql, String message);

  int queryInt(String sql, String message);

  String queryText(String sql, String message);
}
