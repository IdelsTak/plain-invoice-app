package com.plaininvoice.invoice.storage.local;

import com.plaininvoice.invoice.storage.*;
import com.plaininvoice.invoice.storage.backup.*;
import com.plaininvoice.invoice.storage.sqlite.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;

public final class LocalStore implements AutoCloseable {

    private final StoreHome home;
    private final SqliteConnectionConfig config;
    private final StoreConnPort port;
    private Connection connection;

    public LocalStore(StoreHome home) {
        this(home, new SqliteConnectionConfig(), new JdbcStoreConnPort());
    }

    public LocalStore(Path directory) {
        this(new StoreHome(directory));
    }

    LocalStore(StoreHome home, SqliteConnectionConfig config) {
        this(home, config, new JdbcStoreConnPort());
    }

    LocalStore(StoreHome home, SqliteConnectionConfig config, StoreConnPort port) {
        this.home = Objects.requireNonNull(home, "store home cannot be null");
        this.config = Objects.requireNonNull(config, "sqlite config cannot be null");
        this.port = Objects.requireNonNull(port, "store connection port cannot be null");
    }

    public Path database() {
        return home.database();
    }

    public StoreBackupArchive backup(Path directory, Instant createdAt) {
        return new CreateStoreBackup().execute(new StoreBackupRequest(home, directory, createdAt));
    }

    public StoreRestoreReport restore(Path archive, boolean dryRun) {
        close();
        return new RestoreStoreBackup().execute(new StoreRestoreRequest(home, archive, dryRun));
    }

    public InvoiceRepository invoices() {
        return new SqliteInvoiceRepo(connect());
    }

    public int busyTimeOut() {
        return config.busyTimeOut();
    }

    @Override
    public void close() {
        if (connection == null) {
            return;
        }
        try {
            port.close(connection);
            connection = null;
        } catch (SQLException ex) {
            throw new IllegalStateException("store close failed", ex);
        }
    }

    Connection connect() {
        try {
            if (connection == null || port.isClosed(connection)) {
                Files.createDirectories(home.directory());
                connection = port.open(database(), config);
            }
            return connection;
        } catch (SQLException ex) {
            throw new IllegalStateException("store open failed", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("store directory setup failed", ex);
        }
    }
}
