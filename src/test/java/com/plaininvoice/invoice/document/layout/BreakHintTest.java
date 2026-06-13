package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class BreakHintTest {

  @Test
  void trimsBefore() {
    assertThat(new BreakHint(" auto ", "avoid", "auto").before(), is("auto"));
  }

  @Test
  void trimsInside() {
    assertThat(new BreakHint("auto", " avoid ", "auto").inside(), is("avoid"));
  }

  @Test
  void trimsAfter() {
    assertThat(new BreakHint("auto", "avoid", " auto ").after(), is("auto"));
  }

  @Test
  void rejectsBlankBefore() {
    assertThrows(IllegalArgumentException.class, () -> new BreakHint(" ", "avoid", "auto"));
  }

  @Test
  void rejectsBlankInside() {
    assertThrows(IllegalArgumentException.class, () -> new BreakHint("auto", " ", "auto"));
  }

  @Test
  void rejectsBlankAfter() {
    assertThrows(IllegalArgumentException.class, () -> new BreakHint("auto", "avoid", " "));
  }
}
