package com.plaininvoice.invoice.storage.backup;

import java.nio.file.*;
import java.time.*;
import java.util.*;

public record StoreBackupArchive(Path path, Instant createdAt, String databaseName) {

    public StoreBackupArchive {
        Objects.requireNonNull(path, "backup archive path cannot be null");
        Objects.requireNonNull(createdAt, "backup archive timestamp cannot be null");
        Objects.requireNonNull(databaseName, "backup database name cannot be null");
        databaseName = databaseName.trim();
        if (databaseName.isEmpty()) {
            throw new IllegalArgumentException("backup database name cannot be blank");
        }
    }
}
