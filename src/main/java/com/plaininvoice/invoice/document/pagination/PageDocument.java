package com.plaininvoice.invoice.document.pagination;

import com.plaininvoice.invoice.document.layout.*;
import java.util.*;

public record PageDocument(LayoutPage page, List<PageFrame> frames) {
  public PageDocument {
    Objects.requireNonNull(page, "page layout cannot be null");
    Objects.requireNonNull(frames, "page frames cannot be null");
    if (frames.isEmpty()) {
      throw new IllegalArgumentException("page document must contain at least one frame");
    }
    frames = List.copyOf(frames);
  }
}
