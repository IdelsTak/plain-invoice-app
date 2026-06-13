package com.plaininvoice.invoice.document.pagination;

import com.plaininvoice.invoice.document.layout.*;
import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

final class PageSamples {

  private PageSamples() {}

  static LayoutDocument layout() {
    var document = new BuildDocument().document(new DocumentRequest(DocumentSamples.invoice(), DocumentSamples.meta()));
    return new BuildLayout().layout(document);
  }

  static LayoutDocument layoutWithLines(int count) {
    var layout = layout();
    return new LayoutDocument(
      layout.meta(),
      layout.page(),
      layout.header(),
      layout.parties(),
      new LineTableToken(lines(count)),
      layout.totals(),
      layout.terms(),
      layout.footer(),
      layout.hints()
    );
  }

  static PageDocument document() {
    return new PageDocument(layout().page(), List.of(frame()));
  }

  static PageFrame frame() {
    return new PageFrame(1, layout().header(), body(true), layout().footer());
  }

  static PageBody body(boolean finalPage) {
    var layout = layout();
    return new PageBody(layout.parties(), layout.lines(), layout.totals(), layout.terms(), finalPage);
  }

  private static List<LineToken> lines(int count) {
    var source = layout().lines().lines().getFirst();
    var lines = new ArrayList<LineToken>();
    for (var i = 1; i <= count; i++) {
      lines.add(new LineToken(i, "Service " + i, source.quantity(), source.taxRate(), source.amounts(), source.breakHint()));
    }
    return lines;
  }
}
