package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.layout.*;
import com.plaininvoice.invoice.document.pagination.*;
import com.plaininvoice.invoice.pricing.*;
import java.nio.charset.*;
import java.time.*;
import java.util.*;

public final class BuildHtml implements HtmlPort {

  @Override
  public HtmlPage html(PageDocument pages) {
    Objects.requireNonNull(pages, "page document cannot be null");
    var out = new StringBuilder();
    out.append("<!doctype html>\n");
    out.append("<html lang=\"en\">\n");
    head(out, pages);
    body(out, pages);
    out.append("</html>");
    return new HtmlPage(out.toString(), StandardCharsets.UTF_8);
  }

  private void head(StringBuilder out, PageDocument pages) {
    var header = pages.frames().getFirst().header();
    out.append("<head>\n");
    out.append("  <meta charset=\"utf-8\">\n");
    out.append("  <title>").append(escaped(header.title())).append(" ").append(escaped(header.number())).append("</title>\n");
    out.append("  <style>\n");
    out.append("    body{font-family:serif;color:#1f2933;margin:0;background:#fff;}\n");
    out.append("    .page{box-sizing:border-box;width:210mm;min-height:297mm;padding:18mm;margin:0 auto;page-break-after:always;}\n");
    out.append("    .page:last-child{page-break-after:auto;}\n");
    out.append("    header,footer{display:flex;justify-content:space-between;gap:1rem;}\n");
    out.append("    .parties{display:grid;grid-template-columns:1fr 1fr;gap:1rem;margin:2rem 0;}\n");
    out.append("    table{width:100%;border-collapse:collapse;margin:1rem 0;}\n");
    out.append("    th,td{border-bottom:1px solid #d9e2ec;padding:.45rem;text-align:left;}\n");
    out.append("    .money{text-align:right;white-space:nowrap;}\n");
    out.append("  </style>\n");
    out.append("</head>\n");
  }

  private void body(StringBuilder out, PageDocument pages) {
    out.append("<body>\n");
    for (var frame : pages.frames()) {
      frame(out, frame);
    }
    out.append("</body>\n");
  }

  private void frame(StringBuilder out, PageFrame frame) {
    out.append("<article class=\"page\" data-page=\"").append(frame.number()).append("\">\n");
    header(out, frame.header());
    parties(out, frame.body().parties());
    lines(out, frame.body().lines());
    if (frame.body().finalPage()) {
      totals(out, frame.body().totals());
      terms(out, frame.body().terms());
    }
    footer(out, frame);
    out.append("</article>\n");
  }

  private void header(StringBuilder out, HeaderToken header) {
    out.append("<header>\n");
    out.append("  <h1>").append(escaped(header.title())).append("</h1>\n");
    out.append("  <dl>\n");
    term(out, "Number", header.number());
    term(out, "Issued", date(header.issuedOn()));
    term(out, "State", header.state());
    out.append("  </dl>\n");
    out.append("</header>\n");
  }

  private void parties(StringBuilder out, PartyBlock parties) {
    out.append("<section class=\"parties\">\n");
    party(out, parties.seller());
    party(out, parties.buyer());
    out.append("</section>\n");
  }

  private void party(StringBuilder out, PartyToken token) {
    var party = token.party();
    out.append("<section class=\"party ").append(escaped(token.role())).append("\">\n");
    out.append("  <h2>").append(escaped(token.role())).append("</h2>\n");
    out.append("  <p>").append(escaped(party.name())).append("</p>\n");
    out.append("  <p>").append(escaped(party.taxId())).append("</p>\n");
    out.append("  <p>").append(escaped(party.email())).append("</p>\n");
    out.append("</section>\n");
  }

  private void lines(StringBuilder out, LineTableToken table) {
    out.append("<table>\n");
    out.append("  <thead><tr><th>#</th><th>Description</th><th>Qty</th><th>Tax</th><th class=\"money\">Unit</th><th class=\"money\">Subtotal</th><th class=\"money\">Total</th></tr></thead>\n");
    out.append("  <tbody>\n");
    for (var line : table.lines()) {
      line(out, line);
    }
    out.append("  </tbody>\n");
    out.append("</table>\n");
  }

  private void line(StringBuilder out, LineToken line) {
    out.append("    <tr>");
    cell(out, Integer.toString(line.position()));
    cell(out, line.description());
    cell(out, quantity(line.quantity()));
    cell(out, percent(line.taxRate()));
    money(out, line.amounts().unitPrice());
    money(out, line.amounts().subtotal());
    money(out, line.amounts().total());
    out.append("</tr>\n");
  }

  private void totals(StringBuilder out, TotalsToken totals) {
    out.append("<section class=\"totals\">\n");
    term(out, "Subtotal", money(totals.subtotal()));
    term(out, "Tax", money(totals.tax()));
    term(out, "Total due", money(totals.totalDue()));
    out.append("</section>\n");
  }

  private void terms(StringBuilder out, TermsToken terms) {
    out.append("<section class=\"terms\">\n");
    term(out, "Due", date(terms.dueDate()));
    term(out, "Terms", terms.note());
    out.append("</section>\n");
  }

  private void footer(StringBuilder out, PageFrame frame) {
    out.append("<footer>");
    out.append("<span>").append(escaped(frame.footer().text())).append("</span>");
    out.append("<span>Page ").append(frame.number()).append("</span>");
    out.append("</footer>\n");
  }

  private void term(StringBuilder out, String name, String value) {
    out.append("    <dt>").append(escaped(name)).append("</dt><dd>").append(escaped(value)).append("</dd>\n");
  }

  private void cell(StringBuilder out, String value) {
    out.append("<td>").append(escaped(value)).append("</td>");
  }

  private void money(StringBuilder out, Money value) {
    out.append("<td class=\"money\">").append(escaped(money(value))).append("</td>");
  }

  private String money(Money value) {
    return value.currencyCode().value() + " " + value.amount().toPlainString();
  }

  private String quantity(Quantity value) {
    return value.value().toPlainString();
  }

  private String percent(Percentage value) {
    return value.value().toPlainString() + "%";
  }

  private String date(LocalDate value) {
    return value.toString();
  }

  private String escaped(String value) {
    return HtmlText.escaped(value);
  }
}
