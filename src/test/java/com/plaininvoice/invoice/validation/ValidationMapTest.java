package com.plaininvoice.invoice.validation;

import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class ValidationMapTest {

  @Test
  void mapsNullPointerToMissingValue() {
    var map = new ValidationMap("invoice.number", "invoice.number.required", Map.of("source", "test"));
    var error = map.map(new NullPointerException("missing invoice number"));
    assertThat(error, instanceOf(MissingValue.class));
  }

  @Test
  void mapsIllegalArgumentToInvalidValue() {
    var map = new ValidationMap("invoice.number", "invoice.number.invalid", Map.of("source", "test"));
    var error = map.map(new IllegalArgumentException("wrong format"));
    assertThat(error, instanceOf(InvalidValue.class));
  }

  @Test
  void mapsIllegalStateToRuleViolation() {
    var map = new ValidationMap("invoice.state", "invoice.state.invalid", Map.of("source", "test"));
    var error = map.map(new IllegalStateException("invalid transition"));
    assertThat(error, instanceOf(RuleViolation.class));
  }

  @Test
  void keepsPathInMappedError() {
    var map = new ValidationMap("invoice.path", "invoice.code", Map.of("source", "test"));
    var error = map.map(new IllegalArgumentException("wrong value"));
    assertThat(error.path(), is("invoice.path"));
  }

  @Test
  void keepsCodeInMappedError() {
    var map = new ValidationMap("invoice.path", "invoice.code", Map.of("source", "test"));
    var error = map.map(new IllegalArgumentException("wrong value"));
    assertThat(error.code(), is("invoice.code"));
  }

  @Test
  void addsReasonToContext() {
    var map = new ValidationMap("invoice.path", "invoice.code", Map.of("source", "test"));
    var error = map.map(new IllegalArgumentException("wrong value"));
    assertThat(error.context().get("reason"), is("wrong value"));
  }

  @Test
  void keepsBaseContextInMappedError() {
    var map = new ValidationMap("invoice.path", "invoice.code", Map.of("source", "test"));
    var error = map.map(new IllegalArgumentException("wrong value"));
    assertThat(error.context().get("source"), is("test"));
  }

  @Test
  void rejectsBlankPath() {
    assertThrows(IllegalArgumentException.class, () -> new ValidationMap(" ", "invoice.code", Map.of("source", "test")));
  }

  @Test
  void rejectsBlankCode() {
    assertThrows(IllegalArgumentException.class, () -> new ValidationMap("invoice.path", " ", Map.of("source", "test")));
  }

  @Test
  void rejectsNullCause() {
    var map = new ValidationMap("invoice.path", "invoice.code", Map.of("source", "test"));
    assertThrows(NullPointerException.class, () -> map.map(null));
  }

  @Test
  void usesUnknownReasonForNullMessage() {
    var map = new ValidationMap("invoice.path", "invoice.code", Map.of("source", "test"));
    var error = map.map(new IllegalArgumentException());
    assertThat(error.context().get("reason"), is("unknown"));
  }
}
