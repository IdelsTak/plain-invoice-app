package com.plaininvoice.invoice.storage.sqlite;

import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class SqliteConnectionConfigTest {

  @Test
  void rejectsNullConnection() {
    assertThrows(NullPointerException.class, () -> new SqliteConnectionConfig().apply((java.sql.Connection) null));
  }

  @Test
  void rejectsNullPragmaPort() {
    assertThrows(NullPointerException.class, () -> new SqliteConnectionConfig().apply((SqlitePragmaPort) null));
  }

  @Test
  void rejectsWhenForeignKeysSetupFails() {
    var pragmas = new FakePragmas();
    pragmas.failExecute("PRAGMA foreign_keys = ON");
    assertThrows(IllegalStateException.class, () -> new SqliteConnectionConfig().apply(pragmas));
  }

  @Test
  void rejectsWhenJournalModeIsUnexpected() {
    var pragmas = basePragmas();
    pragmas.text("PRAGMA journal_mode = DELETE", "wal");
    assertThrows(IllegalStateException.class, () -> new SqliteConnectionConfig().apply(pragmas));
  }

  @Test
  void rejectsWhenForeignKeysRemainDisabled() {
    var pragmas = basePragmas();
    pragmas.integer("PRAGMA foreign_keys", 0);
    assertThrows(IllegalStateException.class, () -> new SqliteConnectionConfig().apply(pragmas));
  }

  @Test
  void rejectsWhenBusyTimeoutReadBackDiffers() {
    var pragmas = basePragmas();
    pragmas.integer("PRAGMA busy_timeout", 1);
    assertThrows(IllegalStateException.class, () -> new SqliteConnectionConfig().apply(pragmas));
  }

  @Test
  void rejectsWhenIntegrityCheckFails() {
    var pragmas = basePragmas();
    pragmas.text("PRAGMA quick_check(1)", "corrupt");
    assertThrows(IllegalStateException.class, () -> new SqliteConnectionConfig().apply(pragmas));
  }

  @Test
  void rejectsWhenIntegerPragmaReturnsNoRow() {
    var pragmas = basePragmas();
    pragmas.emptyInt("PRAGMA foreign_keys");
    assertThrows(IllegalStateException.class, () -> new SqliteConnectionConfig().apply(pragmas));
  }

  @Test
  void rejectsWhenIntegerPragmaReadFails() {
    var pragmas = basePragmas();
    pragmas.failInt("PRAGMA foreign_keys");
    assertThrows(IllegalStateException.class, () -> new SqliteConnectionConfig().apply(pragmas));
  }

  @Test
  void rejectsWhenTextPragmaReturnsNoRow() {
    var pragmas = basePragmas();
    pragmas.emptyText("PRAGMA quick_check(1)");
    assertThrows(IllegalStateException.class, () -> new SqliteConnectionConfig().apply(pragmas));
  }

  @Test
  void rejectsWhenTextPragmaReadFails() {
    var pragmas = basePragmas();
    pragmas.failText("PRAGMA quick_check(1)");
    assertThrows(IllegalStateException.class, () -> new SqliteConnectionConfig().apply(pragmas));
  }

  private FakePragmas basePragmas() {
    var pragmas = new FakePragmas();
    pragmas.integer("PRAGMA foreign_keys", 1);
    pragmas.text("PRAGMA journal_mode = DELETE", "delete");
    pragmas.integer("PRAGMA busy_timeout", 5000);
    pragmas.text("PRAGMA quick_check(1)", "ok");
    return pragmas;
  }

  private static final class FakePragmas implements SqlitePragmaPort {
    private final Map<String, Integer> ints = new HashMap<>();
    private final Map<String, String> texts = new HashMap<>();
    private final Set<String> executeFailures = new HashSet<>();
    private final Set<String> intFailures = new HashSet<>();
    private final Set<String> textFailures = new HashSet<>();
    private final Set<String> emptyInts = new HashSet<>();
    private final Set<String> emptyTexts = new HashSet<>();

    @Override
    public void execute(String sql, String message) {
      if (executeFailures.contains(sql)) {
        throw new IllegalStateException(message);
      }
    }

    @Override
    public int queryInt(String sql, String message) {
      if (intFailures.contains(sql) || emptyInts.contains(sql)) {
        throw new IllegalStateException(message);
      }
      return ints.get(sql);
    }

    @Override
    public String queryText(String sql, String message) {
      if (textFailures.contains(sql) || emptyTexts.contains(sql)) {
        throw new IllegalStateException(message);
      }
      return texts.get(sql);
    }

    void integer(String sql, int value) {
      ints.put(sql, value);
    }

    void text(String sql, String value) {
      texts.put(sql, value);
    }

    void failExecute(String sql) {
      executeFailures.add(sql);
    }

    void failInt(String sql) {
      intFailures.add(sql);
    }

    void failText(String sql) {
      textFailures.add(sql);
    }

    void emptyInt(String sql) {
      emptyInts.add(sql);
    }

    void emptyText(String sql) {
      emptyTexts.add(sql);
    }
  }
}
