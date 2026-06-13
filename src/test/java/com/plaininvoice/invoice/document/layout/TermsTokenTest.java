package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import java.time.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class TermsTokenTest {

  @Test
  void keepsDueDate() {
    var dueDate = LocalDate.of(2026, 6, 24);
    assertThat(new TermsToken(dueDate, "Net 30").dueDate(), is(dueDate));
  }

  @Test
  void trimsNote() {
    assertThat(new TermsToken(LocalDate.of(2026, 6, 24), " Net 30 ").note(), is("Net 30"));
  }

  @Test
  void rejectsNullDueDate() {
    assertThrows(NullPointerException.class, () -> new TermsToken(null, "Net 30"));
  }

  @Test
  void rejectsNullNote() {
    assertThrows(NullPointerException.class, () -> new TermsToken(LocalDate.of(2026, 6, 24), null));
  }
}
