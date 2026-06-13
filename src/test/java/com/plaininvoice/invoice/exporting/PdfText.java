package com.plaininvoice.invoice.exporting;

import java.io.*;
import java.util.logging.*;
import org.apache.pdfbox.*;
import org.apache.pdfbox.text.*;

final class PdfText {

  static {
    Logger.getLogger("org.apache.pdfbox.pdmodel.font.FileSystemFontProvider").setLevel(Level.SEVERE);
    Logger.getLogger("org.apache.fontbox.ttf.TTFParser").setLevel(Level.SEVERE);
  }

  private PdfText() {}

  static String from(PdfFile file) {
    try (var document = Loader.loadPDF(file.bytes())) {
      return new PDFTextStripper().getText(document);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to read PDF text", ex);
    }
  }
}
