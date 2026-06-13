package com.plaininvoice.invoice.storage.backup;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.zip.*;

public final class CreateStoreBackup {
  private static final String FORMAT = "plain-invoice-backup-v1";
  private static final DateTimeFormatter STAMP = DateTimeFormatter
    .ofPattern("yyyyMMdd'T'HHmmss'Z'")
    .withZone(ZoneOffset.UTC);

  public StoreBackupArchive execute(StoreBackupRequest request) {
    Objects.requireNonNull(request, "store backup request cannot be null");
    var source = request.home().database();
    if (!Files.isRegularFile(source)) {
      throw new IllegalStateException("store database not found");
    }
    try {
      Files.createDirectories(request.directory());
      var copy = Files.createTempFile(request.directory(), "plain-invoice-", ".sqlite");
      try {
        copy(request, copy);
        var archive = archive(request);
        zip(request, copy, archive);
        return new StoreBackupArchive(archive, request.createdAt(), request.home().databaseName());
      } finally {
        Files.deleteIfExists(copy);
      }
    } catch (SQLException ex) {
      throw new IllegalStateException("store backup failed", ex);
    } catch (IOException ex) {
      throw new IllegalStateException("store backup archive failed", ex);
    }
  }

  private void copy(StoreBackupRequest request, Path target) throws SQLException {
    try (
      var connection = DriverManager.getConnection("jdbc:sqlite:" + request.home().database().toAbsolutePath());
      var stmt = connection.createStatement()
    ) {
      stmt.execute("backup main to " + quote(target.toAbsolutePath().toString()));
    }
  }

  private Path archive(StoreBackupRequest request) {
    var base = "plain-invoice-" + STAMP.format(request.createdAt());
    var candidate = request.directory().resolve(base + ".zip");
    var sequence = 2;
    while (Files.exists(candidate)) {
      candidate = request.directory().resolve(base + "-" + sequence + ".zip");
      sequence++;
    }
    return candidate;
  }

  private void zip(StoreBackupRequest request, Path copy, Path archive) throws IOException {
    try (var out = new ZipOutputStream(Files.newOutputStream(archive))) {
      entry(out, "metadata.properties", metadata(request));
      entry(out, request.home().databaseName(), Files.readAllBytes(copy));
    }
  }

  private byte[] metadata(StoreBackupRequest request) {
    return String.join(
      System.lineSeparator(),
      "format=" + FORMAT,
      "created_at=" + request.createdAt(),
      "database_name=" + request.home().databaseName(),
      "schema_version=1",
      ""
    ).getBytes(java.nio.charset.StandardCharsets.UTF_8);
  }

  private void entry(ZipOutputStream out, String name, byte[] bytes) throws IOException {
    out.putNextEntry(new ZipEntry(name));
    out.write(bytes);
    out.closeEntry();
  }

  private String quote(String text) {
    return "\"" + text + "\"";
  }
}
