package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.printable.*;
import java.nio.charset.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class BuildCsvTest {

  @Test
  void usesUtf8() {
    assertThat(csv(ExportCases.simple()).charset(), is(StandardCharsets.UTF_8));
  }

  @Test
  void usesCrLf() {
    assertThat(csv(ExportCases.simple()).value(), containsString("\r\n"));
  }

  @Test
  void includesHeader() {
    assertThat(csv(ExportCases.simple()).value(), startsWith("invoice_number,issued_on,due_date"));
  }

  @Test
  void includesInvoiceNumber() {
    assertThat(csv(ExportCases.simple()).value(), containsString("INV-2026-1000"));
  }

  @Test
  void includesIssueDate() {
    assertThat(csv(ExportCases.simple()).value(), containsString("2026-05-24"));
  }

  @Test
  void includesDueDate() {
    assertThat(csv(ExportCases.simple()).value(), containsString("2026-06-24"));
  }

  @Test
  void includesCurrency() {
    assertThat(csv(ExportCases.simple()).value(), containsString(",USD,"));
  }

  @Test
  void includesTaxedAmount() {
    assertThat(csv(ExportCases.taxed()).value(), containsString(",32.00,232.00,232.00"));
  }

  @Test
  void includesRounding() {
    assertThat(csv(ExportCases.rounding()).value(), containsString(",13.33,1.00,14.33,14.33"));
  }

  @Test
  void writesOneRowPerLine() {
    assertThat(csv(ExportCases.multiLine()).value().lines().count(), is(5L));
  }

  @Test
  void coversAllFixtures() {
    assertThat(ExportCases.all().stream().map(this::csv).map(CsvFile::value).toList(), hasSize(5));
  }

  @Test
  void quotesComma() {
    assertThat(new BuildCsv().csv(EvilCsv.document()).value(), containsString("\"Comma, value\""));
  }

  @Test
  void doublesQuote() {
    assertThat(new BuildCsv().csv(EvilCsv.document()).value(), containsString("\"Quote \"\" value\""));
  }

  @Test
  void quotesNewline() {
    assertThat(new BuildCsv().csv(EvilCsv.document()).value(), containsString("\"Line\nvalue\""));
  }

  @Test
  void keepsEmptyField() {
    assertThat(new BuildCsv().csv(EvilCsv.document()).value(), containsString(",,"));
  }

  @Test
  void keepsNonAsciiText() {
    assertThat(new BuildCsv().csv(EvilCsv.document()).value(), containsString("Café"));
  }

  @Test
  void keepsOutputStable() {
    var port = new BuildCsv();
    assertThat(port.csv(ExportCases.multiLine().document()), is(port.csv(ExportCases.multiLine().document())));
  }

  @Test
  void matchesGoldenText() {
    var actual = csv(ExportCases.simple()).value();
    var expected = "invoice_number,issued_on,due_date,payment_terms,seller_name,seller_tax_id,buyer_name,buyer_tax_id,line_number,description,quantity,tax_rate,currency,unit_price,subtotal,tax,total,invoice_total\r\n";
    assertThat(new GoldenText(expected).compare(actual.substring(0, expected.length())).matched(), is(true));
  }

  @Test
  void rejectsNullDocument() {
    assertThrows(NullPointerException.class, () -> new BuildCsv().csv(null));
  }

  private CsvFile csv(ExportCase sample) {
    return new BuildCsv().csv(sample.document());
  }
}
