package com.plaininvoice.invoice.exporting;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class PdfFileTest {

  @Test
  void keepsContentType() {
    assertThat(new PdfFile(new byte[] {1}, "application/pdf").contentType(), is("application/pdf"));
  }

  @Test
  void trimsContentType() {
    assertThat(new PdfFile(new byte[] {1}, " application/pdf ").contentType(), is("application/pdf"));
  }

  @Test
  void copiesInputBytes() {
    var bytes = new byte[] {1};
    var file = new PdfFile(bytes, "application/pdf");
    bytes[0] = 2;
    assertThat(file.bytes()[0], is((byte) 1));
  }

  @Test
  void copiesOutputBytes() {
    var file = new PdfFile(new byte[] {1}, "application/pdf");
    var bytes = file.bytes();
    bytes[0] = 2;
    assertThat(file.bytes()[0], is((byte) 1));
  }

  @Test
  void rejectsEmptyBytes() {
    assertThrows(IllegalArgumentException.class, () -> new PdfFile(new byte[] {}, "application/pdf"));
  }

  @Test
  void rejectsBlankType() {
    assertThrows(IllegalArgumentException.class, () -> new PdfFile(new byte[] {1}, " "));
  }
}
