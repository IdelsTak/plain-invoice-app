package com.plaininvoice.invoice.document.pagination;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class PaginateLayoutTest {

  @Test
  void createsSinglePageByDefault() {
    var pages = new PaginateLayout().paginate(PageSamples.layout());
    assertThat(pages.frames(), hasSize(1));
  }

  @Test
  void preservesPageLayout() {
    var layout = PageSamples.layout();
    var pages = new PaginateLayout().paginate(layout);
    assertThat(pages.page(), is(layout.page()));
  }

  @Test
  void numbersFirstPage() {
    var pages = new PaginateLayout(new PageRules(1)).paginate(PageSamples.layout());
    assertThat(pages.frames().getFirst().number(), is(1));
  }

  @Test
  void createsMultiplePages() {
    var pages = new PaginateLayout(new PageRules(2)).paginate(PageSamples.layoutWithLines(5));
    assertThat(pages.frames(), hasSize(3));
  }

  @Test
  void limitsPageLines() {
    var pages = new PaginateLayout(new PageRules(2)).paginate(PageSamples.layoutWithLines(5));
    assertThat(pages.frames().getFirst().body().lines().lines(), hasSize(2));
  }

  @Test
  void keepsLastPageRemainder() {
    var pages = new PaginateLayout(new PageRules(2)).paginate(PageSamples.layoutWithLines(5));
    assertThat(pages.frames().get(2).body().lines().lines(), hasSize(1));
  }

  @Test
  void marksFirstPageNonFinal() {
    var pages = new PaginateLayout(new PageRules(1)).paginate(PageSamples.layout());
    assertThat(pages.frames().getFirst().body().finalPage(), is(false));
  }

  @Test
  void marksLastPageFinal() {
    var pages = new PaginateLayout(new PageRules(1)).paginate(PageSamples.layout());
    assertThat(pages.frames().get(1).body().finalPage(), is(true));
  }

  @Test
  void keepsFooterOnPages() {
    var pages = new PaginateLayout(new PageRules(1)).paginate(PageSamples.layout());
    assertThat(pages.frames().getFirst().footer().text(), is("Invoice INV-1000"));
  }

  @Test
  void buildsDeterministicPages() {
    var port = new PaginateLayout(new PageRules(2));
    var layout = PageSamples.layoutWithLines(5);
    assertThat(port.paginate(layout), is(port.paginate(layout)));
  }

  @Test
  void rejectsNullRules() {
    assertThrows(NullPointerException.class, () -> new PaginateLayout(null));
  }

  @Test
  void rejectsNullLayout() {
    var port = new PaginateLayout();
    assertThrows(NullPointerException.class, () -> port.paginate(null));
  }
}
