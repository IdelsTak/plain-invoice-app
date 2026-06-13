package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class LineTableTokenTest {

  @Test
  void copiesLines() {
    var lines = new ArrayList<>(List.of(LayoutSamples.line()));
    var table = new LineTableToken(lines);
    lines.clear();
    assertThat(table.lines(), hasSize(1));
  }

  @Test
  void returnsImmutableLines() {
    var table = new LineTableToken(List.of(LayoutSamples.line()));
    assertThrows(UnsupportedOperationException.class, () -> table.lines().clear());
  }

  @Test
  void rejectsEmptyLines() {
    assertThrows(IllegalArgumentException.class, () -> new LineTableToken(List.of()));
  }
}
