package com.plaininvoice.invoice.document.pagination;

import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class PageDocumentTest {

  @Test
  void keepsPage() {
    var page = PageSamples.layout().page();
    assertThat(new PageDocument(page, List.of(PageSamples.frame())).page(), is(page));
  }

  @Test
  void copiesFrames() {
    var frames = new ArrayList<>(List.of(PageSamples.frame()));
    var document = new PageDocument(PageSamples.layout().page(), frames);
    frames.clear();
    assertThat(document.frames(), hasSize(1));
  }

  @Test
  void returnsImmutableFrames() {
    var document = PageSamples.document();
    assertThrows(UnsupportedOperationException.class, () -> document.frames().clear());
  }

  @Test
  void rejectsEmptyFrames() {
    assertThrows(IllegalArgumentException.class, () -> new PageDocument(PageSamples.layout().page(), List.of()));
  }
}
