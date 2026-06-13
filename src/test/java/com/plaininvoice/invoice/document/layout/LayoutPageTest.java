package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class LayoutPageTest {

  @Test
  void trimsSize() {
    assertThat(new LayoutPage(" A4 ", "portrait", LayoutSamples.margins()).size(), is("A4"));
  }

  @Test
  void trimsOrientation() {
    assertThat(new LayoutPage("A4", " portrait ", LayoutSamples.margins()).orientation(), is("portrait"));
  }

  @Test
  void rejectsBlankSize() {
    assertThrows(IllegalArgumentException.class, () -> new LayoutPage(" ", "portrait", LayoutSamples.margins()));
  }

  @Test
  void rejectsBlankOrientation() {
    assertThrows(IllegalArgumentException.class, () -> new LayoutPage("A4", " ", LayoutSamples.margins()));
  }
}
