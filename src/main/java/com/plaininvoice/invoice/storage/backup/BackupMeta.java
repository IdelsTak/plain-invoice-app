package com.plaininvoice.invoice.storage.backup;

import java.time.*;
import java.util.*;

record BackupMeta(String format, String createdAtText, String databaseName, String schemaText, String sha256) {

    BackupMeta {
        format = text(format, "restore format metadata missing");
        createdAtText = text(createdAtText, "restore timestamp metadata missing");
        databaseName = text(databaseName, "restore database metadata missing");
        schemaText = text(schemaText, "restore schema metadata missing");
        sha256 = text(sha256, "restore checksum metadata missing");
    }

    Instant createdAt() {
        return Instant.parse(createdAtText);
    }

    int schema() {
        return Integer.parseInt(schemaText);
    }

    static String text(String value, String message) {
        Objects.requireNonNull(value, message);
        value = value.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
