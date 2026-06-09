package com.plaininvoice.invoice.storage;

import java.nio.file.*;
import java.util.*;

public record StoreHome(Path directory, String databaseName) {
  private static final String DEFAULT_DATABASE = "plain-invoice.sqlite";

  public StoreHome(Path directory) {
    this(directory, DEFAULT_DATABASE);
  }

  public StoreHome {
    Objects.requireNonNull(directory, "store directory cannot be null");
    Objects.requireNonNull(databaseName, "database name cannot be null");
    databaseName = databaseName.trim();
    if (databaseName.isEmpty()) {
      throw new IllegalArgumentException("database name cannot be blank");
    }
    if (!Path.of(databaseName).getFileName().toString().equals(databaseName)) {
      throw new IllegalArgumentException("database name cannot include a path");
    }
  }

  public Path database() {
    return directory.resolve(databaseName);
  }
}
