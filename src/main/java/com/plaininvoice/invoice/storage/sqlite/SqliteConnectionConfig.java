package com.plaininvoice.invoice.storage.sqlite;

import java.sql.*;
import java.util.*;

public final class SqliteConnectionConfig {

    private static final int BUSY_TIMEOUT_MS = 5000;

    public void apply(Connection connection) {
        Objects.requireNonNull(connection, "connection cannot be null");
        apply(new JdbcSqlitePragmaPort(connection));
    }

    public int busyTimeOut() {
        return BUSY_TIMEOUT_MS;
    }

    void apply(SqlitePragmaPort pragmas) {
        var pragmaPort = Objects.requireNonNull(pragmas, "sqlite pragma port cannot be null");
        foreignKeys(pragmaPort);
        journalMode(pragmaPort);
        busyTimeout(pragmaPort);
        quickCheck(pragmaPort);
    }

    private void foreignKeys(SqlitePragmaPort pragmas) {
        pragmas.execute("PRAGMA foreign_keys = ON", "foreign keys setup failed");
        if (pragmas.queryInt("PRAGMA foreign_keys", "foreign keys read failed") != 1) {
            throw new IllegalStateException("foreign keys setup failed");
        }
    }

    private void journalMode(SqlitePragmaPort pragmas) {
        var mode = pragmas.queryText("PRAGMA journal_mode = DELETE", "journal mode setup failed");
        if (!"delete".equalsIgnoreCase(mode)) {
            throw new IllegalStateException("journal mode setup failed");
        }
    }

    private void busyTimeout(SqlitePragmaPort pragmas) {
        pragmas.execute("PRAGMA busy_timeout = " + BUSY_TIMEOUT_MS, "busy timeout setup failed");
        if (pragmas.queryInt("PRAGMA busy_timeout", "busy timeout read failed") != BUSY_TIMEOUT_MS) {
            throw new IllegalStateException("busy timeout setup failed");
        }
    }

    private void quickCheck(SqlitePragmaPort pragmas) {
        var result = pragmas.queryText("PRAGMA quick_check(1)", "integrity check failed");
        if (!"ok".equalsIgnoreCase(result)) {
            throw new IllegalStateException("integrity check failed");
        }
    }
}
