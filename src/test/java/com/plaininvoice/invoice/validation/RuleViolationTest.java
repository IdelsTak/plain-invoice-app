package com.plaininvoice.invoice.validation;

import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class RuleViolationTest {

  @Test
  void rejectsBlankPath() {
    assertThrows(IllegalArgumentException.class, () -> new RuleViolation("  ", "code", Map.of()));
  }

  @Test
  void rejectsBlankCode() {
    assertThrows(IllegalArgumentException.class, () -> new RuleViolation("path", "  ", Map.of()));
  }
}
