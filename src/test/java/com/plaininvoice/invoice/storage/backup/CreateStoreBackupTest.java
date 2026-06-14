package com.plaininvoice.invoice.storage.backup;

import com.plaininvoice.invoice.storage.local.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.zip.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

final class CreateStoreBackupTest {

  @Test
  void rejectsNullRequest() {
    assertThrows(NullPointerException.class, () -> new CreateStoreBackup().execute(null));
  }

  @Test
  void rejectsMissingDatabase(@TempDir Path temp) {
    var request = new StoreBackupRequest(new StoreHome(temp.resolve("store")), temp.resolve("backups"), now());
    assertThrows(IllegalStateException.class, () -> new CreateStoreBackup().execute(request));
  }

  @Test
  void createsArchive(@TempDir Path temp) throws Exception {
    var archive = backup(temp);
    assertThat(Files.isRegularFile(archive.path()), is(true));
  }

  @Test
  void namesArchiveByTimestamp(@TempDir Path temp) throws Exception {
    var archive = backup(temp);
    assertThat(archive.path().getFileName().toString(), is("plain-invoice-20260524T101530Z.zip"));
  }

  @Test
  void sequencesDuplicateName(@TempDir Path temp) throws Exception {
    Files.createDirectories(temp.resolve("backups"));
    Files.createFile(temp.resolve("backups").resolve("plain-invoice-20260524T101530Z.zip"));
    var archive = backup(temp);
    assertThat(archive.path().getFileName().toString(), is("plain-invoice-20260524T101530Z-2.zip"));
  }

  @Test
  void recordsDatabaseName(@TempDir Path temp) throws Exception {
    var archive = backup(temp);
    assertThat(archive.databaseName(), is("plain-invoice.sqlite"));
  }

  @Test
  void includesDatabaseEntry(@TempDir Path temp) throws Exception {
    var archive = backup(temp);
    assertThat(entry(archive.path(), "plain-invoice.sqlite"), notNullValue());
  }

  @Test
  void includesMetadata(@TempDir Path temp) throws Exception {
    var archive = backup(temp);
    assertThat(text(archive.path(), "metadata.properties"), containsString("format=plain-invoice-backup-v1"));
  }

  @Test
  void recordsTimestamp(@TempDir Path temp) throws Exception {
    var archive = backup(temp);
    assertThat(text(archive.path(), "metadata.properties"), containsString("created_at=2026-05-24T10:15:30Z"));
  }

  @Test
  void recordsChecksum(@TempDir Path temp) throws Exception {
    var archive = backup(temp);
    assertThat(text(archive.path(), "metadata.properties"), containsString("database_sha256="));
  }

  @Test
  void copiesDatabaseRows(@TempDir Path temp) throws Exception {
    var archive = backup(temp);
    assertThat(rowCount(temp, archive.path()), is(1));
  }

  @Test
  void failsWhenArchiveDirectoryIsFile(@TempDir Path temp) throws Exception {
    var home = source(temp);
    var target = Files.createFile(temp.resolve("backups"));
    assertThrows(IllegalStateException.class, () -> execute(home, target));
  }

  @Test
  void failsWhenSourceIsInvalid(@TempDir Path temp) throws Exception {
    var store = temp.resolve("store");
    Files.createDirectories(store);
    Files.writeString(store.resolve("plain-invoice.sqlite"), "not a database");
    assertThrows(IllegalStateException.class, () -> execute(new StoreHome(store), temp.resolve("backups")));
  }

  @Test
  void supportsQuotedPaths(@TempDir Path temp) throws Exception {
    var store = source(temp.resolve("store's"));
    var archive = execute(store, temp.resolve("backup's"));
    assertThat(Files.isRegularFile(archive.path()), is(true));
  }

  private StoreBackupArchive backup(Path temp) throws Exception {
    return execute(source(temp), temp.resolve("backups"));
  }

  private StoreBackupArchive execute(StoreHome home, Path directory) {
    return new CreateStoreBackup().execute(new StoreBackupRequest(home, directory, now()));
  }

  private StoreHome source(Path temp) throws Exception {
    var home = new StoreHome(temp.resolve("store"));
    Files.createDirectories(home.directory());
    try (var connection = DriverManager.getConnection("jdbc:sqlite:" + home.database().toAbsolutePath())) {
      try (var stmt = connection.createStatement()) {
        stmt.execute("CREATE TABLE sample(id INTEGER PRIMARY KEY, name TEXT NOT NULL)");
        stmt.execute("INSERT INTO sample(name) VALUES('saved')");
      }
    }
    return home;
  }

  private Instant now() {
    return Instant.parse("2026-05-24T10:15:30Z");
  }

  private ZipEntry entry(Path archive, String name) throws Exception {
    try (var zip = new ZipFile(archive.toFile())) {
      return zip.getEntry(name);
    }
  }

  private String text(Path archive, String name) throws Exception {
    try (var zip = new ZipFile(archive.toFile())) {
      try (var in = zip.getInputStream(zip.getEntry(name))) {
        return new String(in.readAllBytes(), StandardCharsets.UTF_8);
      }
    }
  }

  private int rowCount(Path temp, Path archive) throws Exception {
    var restored = temp.resolve("restored.sqlite");
    try (var zip = new ZipFile(archive.toFile())) {
      try (var in = zip.getInputStream(zip.getEntry("plain-invoice.sqlite"))) {
        Files.copy(in, restored);
      }
    }
    try (var connection = DriverManager.getConnection("jdbc:sqlite:" + restored.toAbsolutePath())) {
      try (var stmt = connection.prepareStatement("SELECT COUNT(*) FROM sample")) {
        try (var rs = stmt.executeQuery()) {
          rs.next();
          return rs.getInt(1);
        }
      }
    }
  }
}
