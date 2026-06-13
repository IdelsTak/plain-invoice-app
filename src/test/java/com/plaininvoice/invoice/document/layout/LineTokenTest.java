package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class LineTokenTest {

  @Test
  void trimsDescription() {
    assertThat(line(1, " Service ").description(), is("Service"));
  }

  @Test
  void keepsBreakHint() {
    var hint = LayoutSamples.breakHint();
    assertThat(line(1, "Service", hint).breakHint(), is(hint));
  }

  @Test
  void rejectsZeroPosition() {
    assertThrows(IllegalArgumentException.class, () -> line(0, "Service"));
  }

  @Test
  void rejectsBlankDescription() {
    assertThrows(IllegalArgumentException.class, () -> line(1, " "));
  }

  private LineToken line(int position, String description) {
    return line(position, description, LayoutSamples.breakHint());
  }

  private LineToken line(int position, String description, BreakHint hint) {
    var source = DocumentSamples.lines().getFirst();
    return new LineToken(
      position,
      description,
      source.quantity(),
      source.taxRate(),
      source.amounts(),
      hint
    );
  }
}
