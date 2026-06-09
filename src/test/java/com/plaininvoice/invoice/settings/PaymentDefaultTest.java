package com.plaininvoice.invoice.settings;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.*;
import org.junit.jupiter.api.*;

final class PaymentDefaultTest {

  @Test
  void rejectsZeroDays() {
    assertThrows(IllegalArgumentException.class, () -> new PaymentDefault(0, "Net 30"));
  }

  @Test
  void rejectsNegativeDays() {
    assertThrows(IllegalArgumentException.class, () -> new PaymentDefault(-1, "Net 30"));
  }

  @Test
  void defaultsNullNote() {
    assertThat(new PaymentDefault(30, null).note(), is(""));
  }

  @Test
  void trimsNote() {
    assertThat(new PaymentDefault(30, " Net 30 ").note(), is("Net 30"));
  }

  @Test
  void rejectsNullIssueDate() {
    assertThrows(NullPointerException.class, () -> new PaymentDefault(30, "Net 30").terms(null));
  }

  @Test
  void computesDueDate() {
    assertThat(new PaymentDefault(30, "Net 30").terms(LocalDate.of(2026, 5, 24)).dueDate(), is(LocalDate.of(2026, 6, 23)));
  }

  @Test
  void appliesNote() {
    assertThat(new PaymentDefault(30, "Net 30").terms(LocalDate.of(2026, 5, 24)).note(), is("Net 30"));
  }
}
