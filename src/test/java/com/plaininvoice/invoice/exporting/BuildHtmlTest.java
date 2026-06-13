package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.pagination.*;
import java.nio.charset.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class BuildHtmlTest {

  @Test
  void usesUtf8() {
    assertThat(html(ExportCases.simple()).charset(), is(StandardCharsets.UTF_8));
  }

  @Test
  void startsWithDoctype() {
    assertThat(html(ExportCases.simple()).value(), startsWith("<!doctype html>"));
  }

  @Test
  void includesInvoiceNumber() {
    assertThat(html(ExportCases.simple()).value(), containsString("INV-2026-1000"));
  }

  @Test
  void includesIssueDate() {
    assertThat(html(ExportCases.simple()).value(), containsString("2026-05-24"));
  }

  @Test
  void includesDueDate() {
    assertThat(html(ExportCases.simple()).value(), containsString("2026-06-24"));
  }

  @Test
  void includesSeller() {
    assertThat(html(ExportCases.simple()).value(), containsString("Plain Invoice Studio Ltd"));
  }

  @Test
  void includesBuyer() {
    assertThat(html(ExportCases.simple()).value(), containsString("Acme Buyer Operations LLC"));
  }

  @Test
  void includesLineDescription() {
    assertThat(html(ExportCases.simple()).value(), containsString("Consulting session"));
  }

  @Test
  void includesMoney() {
    assertThat(html(ExportCases.simple()).value(), containsString("USD 120.00"));
  }

  @Test
  void includesTax() {
    assertThat(html(ExportCases.taxed()).value(), containsString("USD 32.00"));
  }

  @Test
  void includesRounding() {
    assertThat(html(ExportCases.rounding()).value(), containsString("USD 13.33"));
  }

  @Test
  void includesAllFixtureCases() {
    assertThat(ExportCases.all().stream().map(this::html).map(HtmlPage::value).toList(), hasSize(5));
  }

  @Test
  void escapesHtmlText() {
    assertThat(new BuildHtml().html(EvilHtml.pages()).value(), containsString("A &amp; B &lt;C&gt;"));
  }

  @Test
  void keepsOutputStable() {
    var port = new BuildHtml();
    assertThat(port.html(ExportCases.multiLine().pages()), is(port.html(ExportCases.multiLine().pages())));
  }

  @Test
  void matchesGoldenText() {
    var actual = html(ExportCases.simple()).value();
    var expected = """
      <!doctype html>
      <html lang="en">
      <head>
        <meta charset="utf-8">
        <title>Invoice INV-2026-1000</title>
      """;
    assertThat(new GoldenText(expected).compare(actual.substring(0, expected.length())).matched(), is(true));
  }

  @Test
  void rendersNonFinalPage() {
    var pages = new PaginateLayout(new PageRules(1)).paginate(ExportCases.multiLine().layout());
    assertThat(new BuildHtml().html(pages).value(), containsString("data-page=\"2\""));
  }

  @Test
  void rejectsNullPages() {
    assertThrows(NullPointerException.class, () -> new BuildHtml().html(null));
  }

  private HtmlPage html(ExportCase sample) {
    return new BuildHtml().html(sample.pages());
  }
}
