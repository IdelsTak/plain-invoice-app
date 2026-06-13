package com.plaininvoice.invoice.document.pagination;

import com.plaininvoice.invoice.document.layout.*;
import java.util.*;

public final class PaginateLayout implements PagePort {

  private final PageRules rules;

  public PaginateLayout() {
    this(new PageRules(24));
  }

  public PaginateLayout(PageRules rules) {
    this.rules = Objects.requireNonNull(rules, "page rules cannot be null");
  }

  @Override
  public PageDocument paginate(LayoutDocument layout) {
    Objects.requireNonNull(layout, "layout document cannot be null");
    var frames = new ArrayList<PageFrame>();
    var lines = layout.lines().lines();
    for (var start = 0; start < lines.size(); start += rules.linesPerPage()) {
      frames.add(frame(layout, frames.size() + 1, lines, start));
    }
    return new PageDocument(layout.page(), frames);
  }

  private PageFrame frame(LayoutDocument layout, int number, List<LineToken> lines, int start) {
    var end = Math.min(start + rules.linesPerPage(), lines.size());
    var finalPage = end == lines.size();
    return new PageFrame(number, layout.header(), body(layout, lines.subList(start, end), finalPage), layout.footer());
  }

  private PageBody body(LayoutDocument layout, List<LineToken> lines, boolean finalPage) {
    return new PageBody(
      layout.parties(),
      new LineTableToken(lines),
      layout.totals(),
      layout.terms(),
      finalPage
    );
  }
}
