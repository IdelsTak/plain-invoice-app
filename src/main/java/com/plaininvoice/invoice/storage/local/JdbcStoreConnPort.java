package com.plaininvoice.invoice.storage.local;

import com.plaininvoice.invoice.storage.sqlite.*;
import java.nio.file.*;
import java.sql.*;

final class JdbcStoreConnPort implements StoreConnPort {

    @Override
    public Connection open(Path database, SqliteConnectionConfig config) throws Exception {
        var connection = DriverManager.getConnection("jdbc:sqlite:" + database.toAbsolutePath());
        config.apply(connection);
        return connection;
    }

    @Override
    public boolean isClosed(Connection connection) throws SQLException {
        return connection.isClosed();
    }

    @Override
    public void close(Connection connection) throws SQLException {
        connection.close();
    }
}
