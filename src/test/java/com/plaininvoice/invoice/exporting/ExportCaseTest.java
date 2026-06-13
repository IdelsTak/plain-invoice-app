package com.plaininvoice.invoice.exporting;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class ExportCaseTest {

  @Test
  void createsDocument() {
    assertThat(ExportCases.simple().document().header().number(), is("INV-2026-1000"));
  }

  @Test
  void createsLayout() {
    assertThat(ExportCases.simple().layout().footer().text(), is("Invoice INV-2026-1000"));
  }

  @Test
  void createsPages() {
    assertThat(ExportCases.simple().pages().frames(), hasSize(1));
  }

  @Test
  void trimsKey() {
    var sample = new ExportCase(" simple ", "purpose", ExportCases.simple().invoice(), ExportCases.simple().meta());
    assertThat(sample.key(), is("simple"));
  }

  @Test
  void rejectsBlankKey() {
    assertThrows(IllegalArgumentException.class, () -> new ExportCase(" ", "purpose", ExportCases.simple().invoice(), ExportCases.simple().meta()));
  }
}
