package com.plaininvoice.invoice.storage.backup;

import com.plaininvoice.invoice.storage.local.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

public record StoreBackupRequest(StoreHome home, Path directory, Instant createdAt) {

    public StoreBackupRequest {
        Objects.requireNonNull(home, "backup store home cannot be null");
        Objects.requireNonNull(directory, "backup directory cannot be null");
        Objects.requireNonNull(createdAt, "backup timestamp cannot be null");
    }
}
