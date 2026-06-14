package com.plaininvoice.invoice.storage.backup;

import com.plaininvoice.invoice.storage.sqlite.*;
import java.nio.file.*;
import java.sql.*;

final class JdbcRestoreSqlitePort implements RestoreSqlitePort {

    @Override
    public String integrity(Path copy) throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + copy.toAbsolutePath())) {
            new SqliteConnectionConfig().apply(connection);
            try (var stmt = connection.createStatement(); var rs = stmt.executeQuery("PRAGMA integrity_check(1)")) {
                rs.next();
                return rs.getString(1);
            }
        }
    }
}
