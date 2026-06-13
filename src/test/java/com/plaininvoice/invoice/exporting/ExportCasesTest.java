package com.plaininvoice.invoice.exporting;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class ExportCasesTest {

  @Test
  void definesFiveCases() {
    assertThat(ExportCases.all(), hasSize(5));
  }

  @Test
  void includesSimpleCase() {
    assertThat(ExportCases.all().stream().map(ExportCase::key).toList(), hasItem("simple"));
  }

  @Test
  void includesMultiLineCase() {
    assertThat(ExportCases.multiLine().invoice().lineItems(), hasSize(4));
  }

  @Test
  void includesTaxedCase() {
    assertThat(ExportCases.taxed().document().totals().tax().amount().toPlainString(), is("32.00"));
  }

  @Test
  void includesRoundingCase() {
    assertThat(ExportCases.rounding().document().totals().subtotal().amount().toPlainString(), is("13.33"));
  }

  @Test
  void includesLongContent() {
    assertThat(ExportCases.longContent().invoice().lineItems().getFirst().description(), containsString("migration notes"));
  }
}
