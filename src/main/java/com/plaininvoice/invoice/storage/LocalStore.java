package com.plaininvoice.invoice.storage;

import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;

public final class LocalStore implements AutoCloseable {
  private final StoreHome home;
  private Connection connection;

  public LocalStore(StoreHome home) {
    this.home = Objects.requireNonNull(home, "store home cannot be null");
  }

  public LocalStore(Path directory) {
    this(new StoreHome(directory));
  }

  public Path database() {
    return home.database();
  }

  public StoreBackupArchive backup(Path directory, Instant createdAt) {
    return new CreateStoreBackup().execute(new StoreBackupRequest(home, directory, createdAt));
  }

  public InvoiceRepository invoices() {
    return new SqliteInvoiceRepo(connect());
  }

  Connection connect() {
    try {
      if (connection == null || connection.isClosed()) {
        Files.createDirectories(home.directory());
        connection = DriverManager.getConnection("jdbc:sqlite:" + database().toAbsolutePath());
      }
      return connection;
    } catch (SQLException ex) {
      throw new IllegalStateException("store open failed", ex);
    } catch (Exception ex) {
      throw new IllegalStateException("store directory setup failed", ex);
    }
  }

  @Override
  public void close() {
    if (connection == null) {
      return;
    }
    try {
      connection.close();
      connection = null;
    } catch (SQLException ex) {
      throw new IllegalStateException("store close failed", ex);
    }
  }
}
