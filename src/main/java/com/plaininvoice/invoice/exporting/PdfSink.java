package com.plaininvoice.invoice.exporting;

import java.io.*;
import org.apache.pdfbox.pdmodel.*;

interface PdfSink {
  void save(PDDocument document, OutputStream out) throws IOException;
}
