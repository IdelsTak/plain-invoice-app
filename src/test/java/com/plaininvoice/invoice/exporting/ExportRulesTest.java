package com.plaininvoice.invoice.exporting;

import java.nio.charset.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class ExportRulesTest {

  @Test
  void htmlUsesUtf8() {
    assertThat(ExportRules.html().charset(), is(StandardCharsets.UTF_8));
  }

  @Test
  void htmlUsesLf() {
    assertThat(ExportRules.html().lineBreak(), is("\n"));
  }

  @Test
  void pdfUsesTextComparison() {
    assertThat(ExportRules.pdfText().comparison(), containsString("extracted text"));
  }

  @Test
  void csvUsesCrLf() {
    assertThat(ExportRules.csv().lineBreak(), is("\r\n"));
  }

  @Test
  void csvUsesExtension() {
    assertThat(ExportRules.csv().extension(), is(".csv"));
  }

  @Test
  void rejectsBlankFormat() {
    assertThrows(IllegalArgumentException.class, () -> new ExportRules(" ", ".txt", StandardCharsets.UTF_8, "\n", "text"));
  }
}
