package com.plaininvoice.invoice.document.printable;

import java.time.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class DocumentTermsTest {

  @Test
  void keepsDueDate() {
    var dueDate = LocalDate.of(2026, 6, 24);
    assertThat(new DocumentTerms(dueDate, "Net 30").dueDate(), is(dueDate));
  }

  @Test
  void trimsNote() {
    assertThat(new DocumentTerms(LocalDate.of(2026, 6, 24), " Net 30 ").note(), is("Net 30"));
  }

  @Test
  void rejectsNullDueDate() {
    assertThrows(NullPointerException.class, () -> new DocumentTerms(null, "Net 30"));
  }

  @Test
  void rejectsNullNote() {
    assertThrows(NullPointerException.class, () -> new DocumentTerms(LocalDate.of(2026, 6, 24), null));
  }
}
