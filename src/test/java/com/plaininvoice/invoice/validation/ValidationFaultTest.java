package com.plaininvoice.invoice.validation;

import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class ValidationFaultTest {

  @Test
  void storesErrors() {
    var fault = new ValidationFault(List.of(new InvalidValue("invoice.number", "invoice.invalid", Map.of("reason", "bad"))));
    assertThat(fault.errors().size(), is(1));
  }

  @Test
  void rejectsEmptyErrors() {
    assertThrows(IllegalArgumentException.class, () -> new ValidationFault(List.of()));
  }

  @Test
  void keepsCause() {
    var cause = new IllegalArgumentException("bad");
    var fault = new ValidationFault(List.of(new InvalidValue("invoice.number", "invoice.invalid", Map.of("reason", "bad"))), cause);
    assertThat(fault.getCause(), is(cause));
  }

  @Test
  void rejectsNullCause() {
    assertThrows(
      NullPointerException.class,
      () -> new ValidationFault(List.of(new InvalidValue("invoice.number", "invoice.invalid", Map.of("reason", "bad"))), null)
    );
  }

  @Test
  void rejectsEmptyErrorsWithCause() {
    assertThrows(IllegalArgumentException.class, () -> new ValidationFault(List.of(), new IllegalArgumentException("bad")));
  }

  @Test
  void rejectsNullErrorsWithoutCause() {
    assertThrows(NullPointerException.class, () -> new ValidationFault(null));
  }
}
