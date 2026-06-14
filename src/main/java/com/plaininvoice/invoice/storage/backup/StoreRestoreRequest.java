package com.plaininvoice.invoice.storage.backup;

import com.plaininvoice.invoice.storage.local.*;
import java.nio.file.*;
import java.util.*;

public record StoreRestoreRequest(StoreHome home, Path archive, boolean dryRun) {

    public StoreRestoreRequest {
        Objects.requireNonNull(home, "restore store home cannot be null");
        Objects.requireNonNull(archive, "restore archive cannot be null");
    }
}
