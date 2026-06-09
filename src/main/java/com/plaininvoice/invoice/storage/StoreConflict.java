package com.plaininvoice.invoice.storage;

public final class StoreConflict extends RuntimeException {
  public StoreConflict(String message) {
    super(message);
  }
}
