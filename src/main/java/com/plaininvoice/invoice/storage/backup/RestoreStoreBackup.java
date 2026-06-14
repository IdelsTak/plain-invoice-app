package com.plaininvoice.invoice.storage.backup;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.*;
import java.sql.*;
import java.util.*;
import java.util.zip.*;

public final class RestoreStoreBackup {

    private static final String FORMAT = "plain-invoice-backup-v1";
    private static final String META = "metadata.properties";
    private static final int SCHEMA = 1;
    private final RestoreSqlitePort sqlite;

    public RestoreStoreBackup() {
        this(new JdbcRestoreSqlitePort());
    }

    RestoreStoreBackup(RestoreSqlitePort sqlite) {
        this.sqlite = Objects.requireNonNull(sqlite, "restore sqlite port cannot be null");
    }

    public StoreRestoreReport execute(StoreRestoreRequest request) {
        Objects.requireNonNull(request, "store restore request cannot be null");
        if (!Files.isRegularFile(request.archive())) {
            throw new IllegalStateException("restore archive not found");
        }
        try {
            var stage = Files.createTempDirectory("plain-invoice-restore-");
            try {
                var meta = readMeta(request.archive());
                checkMeta(meta);
                var bytes = readDb(request.archive(), meta);
                checkSum(meta, bytes);
                var copy = stage.resolve(meta.databaseName());
                Files.write(copy, bytes);
                checkDb(copy);
                if (!request.dryRun()) {
                    applyCopy(request, copy);
                }
                return new StoreRestoreReport(
                  request.archive(),
                  request.home().database(),
                  meta.createdAt(),
                  meta.databaseName(),
                  request.dryRun()
                );
            } finally {
                dropTree(stage);
            }
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("store restore failed", ex);
        }
    }

    private BackupMeta readMeta(Path archive) throws IOException {
        try (var zip = new ZipFile(archive.toFile())) {
            var entry = zip.getEntry(META);
            if (entry == null) {
                throw new IllegalStateException("restore metadata missing");
            }
            var props = new Properties();
            try (var in = zip.getInputStream(entry); var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                props.load(reader);
            }
            return new BackupMeta(
              props.getProperty("format"),
              props.getProperty("created_at"),
              props.getProperty("database_name"),
              props.getProperty("schema_version"),
              props.getProperty("database_sha256")
            );
        }
    }

    private void checkMeta(BackupMeta meta) {
        if (!FORMAT.equals(meta.format())) {
            throw new IllegalStateException("restore format not supported");
        }
        meta.createdAt();
        if (meta.schema() != SCHEMA) {
            throw new IllegalStateException("restore schema version not supported");
        }
    }

    private byte[] readDb(Path archive, BackupMeta meta) throws IOException {
        try (var zip = new ZipFile(archive.toFile())) {
            var entry = zip.getEntry(meta.databaseName());
            if (entry == null) {
                throw new IllegalStateException("restore database entry missing");
            }
            try (var in = zip.getInputStream(entry)) {
                return in.readAllBytes();
            }
        }
    }

    private void checkSum(BackupMeta meta, byte[] bytes) throws NoSuchAlgorithmException {
        if (!meta.sha256().equals(sha256(bytes))) {
            throw new IllegalStateException("restore checksum mismatch");
        }
    }

    private void checkDb(Path copy) throws SQLException {
        try {
            if (!"ok".equalsIgnoreCase(sqlite.integrity(copy))) {
                throw new IllegalStateException("restore integrity check failed");
            }
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("restore integrity check failed", ex);
        }
    }

    private void applyCopy(StoreRestoreRequest request, Path copy) throws IOException {
        Files.createDirectories(request.home().directory());
        Files.move(copy, request.home().database(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void dropTree(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException {
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private String sha256(byte[] bytes) throws NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(bytes));
    }
}
