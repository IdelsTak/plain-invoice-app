package com.plaininvoice.invoice.storage.local;

import com.plaininvoice.invoice.storage.sqlite.*;
import java.nio.file.*;
import java.sql.*;

interface StoreConnPort {

    Connection open(Path database, SqliteConnectionConfig config) throws Exception;

    boolean isClosed(Connection connection) throws SQLException;

    void close(Connection connection) throws SQLException;
}
