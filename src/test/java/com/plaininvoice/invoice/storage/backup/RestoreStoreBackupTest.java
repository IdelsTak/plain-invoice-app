package com.plaininvoice.invoice.storage.backup;

import com.plaininvoice.invoice.storage.local.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.zip.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class RestoreStoreBackupTest {

    @Test
    void rejectsNullRequest() {
        assertThrows(NullPointerException.class, () -> new RestoreStoreBackup().execute(null));
    }

    @Test
    void rejectsNullSqlitePort() {
        assertThrows(NullPointerException.class, () -> new RestoreStoreBackup(null));
    }

    @Test
    void restoresCompatibleArchive(@TempDir Path temp) throws Exception {
        var archive = backup(temp.resolve("source"));
        var home = target(temp.resolve("target"), "stale");
        var report = restore(home, archive, false);
        assertThat(rowValue(home.database()), is("saved"));
        assertThat(report.databaseName(), is("plain-invoice.sqlite"));
    }

    @Test
    void reportsDryRunWithoutChangingStore(@TempDir Path temp) throws Exception {
        var archive = backup(temp.resolve("source"));
        var home = target(temp.resolve("target"), "stale");
        var report = restore(home, archive, true);
        assertThat(rowValue(home.database()), is("stale"));
        assertThat(report.dryRun(), is(true));
    }

    @Test
    void rejectsIncompatibleSchemaVersion(@TempDir Path temp) throws Exception {
        var archive = rewriteMeta(backup(temp.resolve("source")).path(), Map.of("schema_version", "2"));
        var home = target(temp.resolve("target"), "stale");
        assertThrows(IllegalStateException.class, () -> restore(home, archive, false));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsChecksumMismatch(@TempDir Path temp) throws Exception {
        var archive = rewriteMeta(backup(temp.resolve("source")).path(), Map.of("database_sha256", "bad"));
        var home = target(temp.resolve("target"), "stale");
        assertThrows(IllegalStateException.class, () -> restore(home, archive, false));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsMissingMetadataEntry(@TempDir Path temp) throws Exception {
        var archive = withoutMeta(backup(temp.resolve("source")).path());
        var home = target(temp.resolve("target"), "stale");
        assertThrows(IllegalStateException.class, () -> restore(home, archive, false));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsMissingDatabaseEntry(@TempDir Path temp) throws Exception {
        var archive = withoutDb(backup(temp.resolve("source")).path());
        var home = target(temp.resolve("target"), "stale");
        assertThrows(IllegalStateException.class, () -> restore(home, archive, false));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsUnsupportedFormat(@TempDir Path temp) throws Exception {
        var archive = rewriteMeta(backup(temp.resolve("source")).path(), Map.of("format", "plain-invoice-backup-v2"));
        var home = target(temp.resolve("target"), "stale");
        assertThrows(IllegalStateException.class, () -> restore(home, archive, false));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsBrokenCreatedAtMetadata(@TempDir Path temp) throws Exception {
        var archive = rewriteMeta(backup(temp.resolve("source")).path(), Map.of("created_at", "nope"));
        var home = target(temp.resolve("target"), "stale");
        assertThrows(IllegalStateException.class, () -> restore(home, archive, false));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsBlankChecksumMetadata(@TempDir Path temp) throws Exception {
        var archive = rewriteMeta(backup(temp.resolve("source")).path(), Map.of("database_sha256", " "));
        var home = target(temp.resolve("target"), "stale");
        assertThrows(IllegalStateException.class, () -> restore(home, archive, false));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsFailedIntegrityCheck(@TempDir Path temp) throws Exception {
        var archive = backup(temp.resolve("source"));
        var home = target(temp.resolve("target"), "stale");
        var restore = new RestoreStoreBackup(_ -> "broken");
        assertThrows(IllegalStateException.class, () ->
          restore.execute(new StoreRestoreRequest(home, archive.path(), false)));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsIntegrityCheckError(@TempDir Path temp) throws Exception {
        var archive = backup(temp.resolve("source"));
        var home = target(temp.resolve("target"), "stale");
        var restore = new RestoreStoreBackup(_ -> {
            throw new SQLException("boom");
        });
        assertThrows(IllegalStateException.class, () ->
          restore.execute(new StoreRestoreRequest(home, archive.path(), false)));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsCorruptSnapshotEvenWithChecksum(@TempDir Path temp) throws Exception {
        var archive = corruptDb(backup(temp.resolve("source")).path());
        var home = target(temp.resolve("target"), "stale");
        assertThrows(IllegalStateException.class, () -> restore(home, archive, false));
        assertThat(rowValue(home.database()), is("stale"));
    }

    @Test
    void rejectsMissingArchive(@TempDir Path temp) {
        var home = new StoreHome(temp.resolve("store"));
        assertThrows(IllegalStateException.class, () ->
          new RestoreStoreBackup().execute(new StoreRestoreRequest(home, temp.resolve("missing.zip"), false)));
    }

    private StoreBackupArchive backup(Path temp) throws Exception {
        var home = source(temp);
        return new CreateStoreBackup().execute(new StoreBackupRequest(home, temp.resolve("backups"), now()));
    }

    private StoreHome source(Path temp) throws Exception {
        var home = new StoreHome(temp.resolve("store"));
        Files.createDirectories(home.directory());
        writeDb(home.database(), "saved");
        return home;
    }

    private StoreHome target(Path temp, String value) throws Exception {
        var home = new StoreHome(temp.resolve("store"));
        Files.createDirectories(home.directory());
        writeDb(home.database(), value);
        return home;
    }

    private StoreRestoreReport restore(StoreHome home, StoreBackupArchive archive, boolean dryRun) {
        return restore(home, archive.path(), dryRun);
    }

    private StoreRestoreReport restore(StoreHome home, Path archive, boolean dryRun) {
        return new RestoreStoreBackup().execute(new StoreRestoreRequest(home, archive, dryRun));
    }

    private void writeDb(Path database, String value) throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database.toAbsolutePath())) {
            try (var stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE sample(id INTEGER PRIMARY KEY, name TEXT NOT NULL)");
                stmt.execute("INSERT INTO sample(name) VALUES('" + value + "')");
            }
        }
    }

    private String rowValue(Path database) throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database.toAbsolutePath())) {
            try (var stmt = connection.prepareStatement("SELECT name FROM sample")) {
                try (var rs = stmt.executeQuery()) {
                    rs.next();
                    return rs.getString(1);
                }
            }
        }
    }

    private Path rewriteMeta(Path archive, Map<String, String> updates) throws Exception {
        var target = archive.resolveSibling("rewritten-" + archive.getFileName());
        var props = props(archive);
        updates.forEach(props::setProperty);
        writeZip(target, propsText(props), dbBytes(archive), props.getProperty("database_name"));
        return target;
    }

    private Path corruptDb(Path archive) throws Exception {
        var target = archive.resolveSibling("corrupt-" + archive.getFileName());
        var props = props(archive);
        var bytes = "not a database".getBytes(StandardCharsets.UTF_8);
        props.setProperty("database_sha256", sha256(bytes));
        writeZip(target, propsText(props), bytes, props.getProperty("database_name"));
        return target;
    }

    private Path withoutMeta(Path archive) throws Exception {
        var target = archive.resolveSibling("missing-meta-" + archive.getFileName());
        writeZip(target, null, dbBytes(archive), props(archive).getProperty("database_name"));
        return target;
    }

    private Path withoutDb(Path archive) throws Exception {
        var target = archive.resolveSibling("missing-db-" + archive.getFileName());
        writeZip(target, propsText(props(archive)), null, props(archive).getProperty("database_name"));
        return target;
    }

    private Properties props(Path archive) throws Exception {
        var props = new Properties();
        try (var zip = new ZipFile(archive.toFile())) {
            try (var in = zip.getInputStream(zip.getEntry("metadata.properties"))) {
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        }
        return props;
    }

    private byte[] dbBytes(Path archive) throws Exception {
        try (var zip = new ZipFile(archive.toFile())) {
            var name = props(archive).getProperty("database_name");
            try (var in = zip.getInputStream(zip.getEntry(name))) {
                return in.readAllBytes();
            }
        }
    }

    private byte[] propsText(Properties props) throws Exception {
        try (var out = new ByteArrayOutputStream()) {
            out.write(("format=" + props.getProperty("format") + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            out.write(("created_at=" + props.getProperty("created_at") + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            out.write(("database_name=" + props.getProperty("database_name") + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            out.write(("schema_version=" + props.getProperty("schema_version") + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            out.write(("database_sha256=" + props.getProperty("database_sha256") + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            return out.toByteArray();
        }
    }

    private void writeZip(Path archive, byte[] meta, byte[] db, String databaseName) throws Exception {
        try (var out = new ZipOutputStream(Files.newOutputStream(archive))) {
            if (meta != null) {
                entry(out, "metadata.properties", meta);
            }
            if (db != null) {
                entry(out, databaseName, db);
            }
        }
    }

    private void entry(ZipOutputStream out, String name, byte[] bytes) throws Exception {
        out.putNextEntry(new ZipEntry(name));
        out.write(bytes);
        out.closeEntry();
    }

    private String sha256(byte[] bytes) throws Exception {
        var digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(bytes));
    }

    private Instant now() {
        return Instant.parse("2026-05-24T10:15:30Z");
    }
}
