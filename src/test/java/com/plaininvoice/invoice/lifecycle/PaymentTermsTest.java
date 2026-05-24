package com.plaininvoice.invoice.lifecycle;

import java.time.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class PaymentTermsTest {

  @Test
  void defaultsNullNoteToEmpty() {
    var terms = new PaymentTerms(LocalDate.of(2026, 6, 30), null);
    assertEquals("", terms.note());
  }

  @Test
  void returnsTrueWhenReferenceDateAfterDueDate() {
    var terms = new PaymentTerms(LocalDate.of(2026, 6, 30), "Net 30");
    assertTrue(terms.overdueOn(LocalDate.of(2026, 7, 1)));
  }

  @Test
  void returnsFalseWhenReferenceDateOnDueDate() {
    var terms = new PaymentTerms(LocalDate.of(2026, 6, 30), "Net 30");
    assertFalse(terms.overdueOn(LocalDate.of(2026, 6, 30)));
  }
}
