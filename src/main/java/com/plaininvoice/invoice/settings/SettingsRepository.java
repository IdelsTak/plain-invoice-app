package com.plaininvoice.invoice.settings;

import java.util.*;

public interface SettingsRepository {
  Optional<SettingsRecord> load();

  void save(SettingsRecord settings);
}
