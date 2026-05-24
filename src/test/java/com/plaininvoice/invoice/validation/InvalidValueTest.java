package com.plaininvoice.invoice.validation;

import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class InvalidValueTest {

  @Test
  void rejectsBlankPath() {
    assertThrows(IllegalArgumentException.class, () -> new InvalidValue("  ", "code", Map.of()));
  }

  @Test
  void rejectsBlankCode() {
    assertThrows(IllegalArgumentException.class, () -> new InvalidValue("path", "  ", Map.of()));
  }
}
