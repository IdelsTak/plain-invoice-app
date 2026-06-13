package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.pagination.*;
import java.io.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class BuildPdfTest {

  @Test
  void usesPdfType() {
    assertThat(pdf(ExportCases.simple()).contentType(), is("application/pdf"));
  }

  @Test
  void writesPdfHeader() {
    assertThat(new String(pdf(ExportCases.simple()).bytes(), 0, 4), is("%PDF"));
  }

  @Test
  void includesInvoiceNumber() {
    assertThat(text(ExportCases.simple()), containsString("INV-2026-1000"));
  }

  @Test
  void includesIssueDate() {
    assertThat(text(ExportCases.simple()), containsString("Issued 2026-05-24"));
  }

  @Test
  void includesDueDate() {
    assertThat(text(ExportCases.simple()), containsString("Due 2026-06-24"));
  }

  @Test
  void includesSeller() {
    assertThat(text(ExportCases.simple()), containsString("seller Plain Invoice Studio Ltd"));
  }

  @Test
  void includesBuyer() {
    assertThat(text(ExportCases.simple()), containsString("buyer Acme Buyer Operations LLC"));
  }

  @Test
  void includesLineDescription() {
    assertThat(text(ExportCases.simple()), containsString("Consulting session"));
  }

  @Test
  void includesMoney() {
    assertThat(text(ExportCases.simple()), containsString("USD 120.00"));
  }

  @Test
  void includesTax() {
    assertThat(text(ExportCases.taxed()), containsString("Tax USD 32.00"));
  }

  @Test
  void includesRounding() {
    assertThat(text(ExportCases.rounding()), containsString("Subtotal USD 13.33"));
  }

  @Test
  void coversAllFixtures() {
    assertThat(ExportCases.all().stream().map(this::pdf).map(PdfFile::bytes).toList(), hasSize(5));
  }

  @Test
  void rendersManyPages() {
    var pages = new PaginateLayout(new PageRules(1)).paginate(ExportCases.multiLine().layout());
    assertThat(PdfText.from(new BuildPdf().pdf(pages)), containsString("Page 2"));
  }

  @Test
  void sanitizesNewline() {
    assertThat(PdfText.from(new BuildPdf().pdf(EvilPdf.pages())), containsString("Line value"));
  }

  @Test
  void keepsOutputReadable() {
    assertThat(PdfText.from(pdf(ExportCases.longContent())), containsString("Extended implementation advisory"));
  }

  @Test
  void rejectsNullPages() {
    assertThrows(NullPointerException.class, () -> new BuildPdf().pdf(null));
  }

  @Test
  void rejectsNullSink() {
    assertThrows(NullPointerException.class, () -> new BuildPdf(null));
  }

  @Test
  void wrapsWriteFailure() {
    assertThrows(IllegalStateException.class, () -> brokenPdf().pdf(ExportCases.simple().pages()));
  }

  private BuildPdf brokenPdf() {
    return new BuildPdf((document, out) -> {
      throw new IOException("forced");
    });
  }

  private PdfFile pdf(ExportCase sample) {
    return new BuildPdf().pdf(sample.pages());
  }

  private String text(ExportCase sample) {
    return PdfText.from(pdf(sample));
  }
}
