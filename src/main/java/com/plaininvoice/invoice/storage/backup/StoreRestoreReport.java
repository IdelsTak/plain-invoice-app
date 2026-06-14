package com.plaininvoice.invoice.storage.backup;

import java.nio.file.*;
import java.time.*;
import java.util.*;

public record StoreRestoreReport(Path archive, Path database, Instant createdAt, String databaseName, boolean dryRun) {

    public StoreRestoreReport {
        Objects.requireNonNull(archive, "restore archive path cannot be null");
        Objects.requireNonNull(database, "restore database path cannot be null");
        Objects.requireNonNull(createdAt, "restore timestamp cannot be null");
        Objects.requireNonNull(databaseName, "restore database name cannot be null");
        databaseName = databaseName.trim();
        if (databaseName.isEmpty()) {
            throw new IllegalArgumentException("restore database name cannot be blank");
        }
    }
}
