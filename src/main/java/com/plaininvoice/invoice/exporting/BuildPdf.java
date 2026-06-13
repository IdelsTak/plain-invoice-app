package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.layout.*;
import com.plaininvoice.invoice.document.pagination.*;
import com.plaininvoice.invoice.pricing.*;
import java.io.*;
import java.util.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.pdfbox.pdmodel.font.*;

public final class BuildPdf implements PdfPort {

  private static final float LEFT = 50;
  private static final float TOP = 790;
  private static final float LEADING = 14;
  private final PdfSink sink;

  public BuildPdf() {
    this(PDDocument::save);
  }

  BuildPdf(PdfSink sink) {
    this.sink = Objects.requireNonNull(sink, "PDF sink cannot be null");
  }

  @Override
  public PdfFile pdf(PageDocument pages) {
    Objects.requireNonNull(pages, "page document cannot be null");
    try (var document = new PDDocument()) {
      var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
      info(document);
      for (var frame : pages.frames()) {
        page(document, font, frame);
      }
      var out = new ByteArrayOutputStream();
      sink.save(document, out);
      return new PdfFile(out.toByteArray(), "application/pdf");
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to build PDF", ex);
    }
  }

  private void info(PDDocument document) {
    var info = document.getDocumentInformation();
    info.setTitle("Plain Invoice");
    info.setAuthor("plain-invoice-app");
    info.setCreator("plain-invoice-app");
    info.setProducer("Apache PDFBox");
  }

  private void page(PDDocument document, PDFont font, PageFrame frame) throws IOException {
    var page = new PDPage(PDRectangle.A4);
    document.addPage(page);
    try (var stream = new PDPageContentStream(document, page)) {
      stream.beginText();
      stream.setFont(font, 10);
      stream.newLineAtOffset(LEFT, TOP);
      lines(stream, frame);
      stream.endText();
    }
  }

  private void lines(PDPageContentStream stream, PageFrame frame) throws IOException {
    line(stream, frame.header().title() + " " + frame.header().number());
    line(stream, "Issued " + frame.header().issuedOn());
    line(stream, "State " + frame.header().state());
    party(stream, frame.body().parties().seller());
    party(stream, frame.body().parties().buyer());
    for (var item : frame.body().lines().lines()) {
      item(stream, item);
    }
    if (frame.body().finalPage()) {
      totals(stream, frame.body().totals());
      terms(stream, frame.body().terms());
    }
    line(stream, frame.footer().text() + " Page " + frame.number());
  }

  private void party(PDPageContentStream stream, PartyToken token) throws IOException {
    var party = token.party();
    line(stream, token.role() + " " + party.name());
    line(stream, party.taxId());
    line(stream, party.email());
  }

  private void item(PDPageContentStream stream, LineToken item) throws IOException {
    line(
      stream,
      item.position()
        + " "
        + item.description()
        + " qty "
        + quantity(item.quantity())
        + " tax "
        + percent(item.taxRate())
        + " total "
        + money(item.amounts().total())
    );
  }

  private void totals(PDPageContentStream stream, TotalsToken totals) throws IOException {
    line(stream, "Subtotal " + money(totals.subtotal()));
    line(stream, "Tax " + money(totals.tax()));
    line(stream, "Total due " + money(totals.totalDue()));
  }

  private void terms(PDPageContentStream stream, TermsToken terms) throws IOException {
    line(stream, "Due " + terms.dueDate());
    line(stream, "Terms " + terms.note());
  }

  private void line(PDPageContentStream stream, String value) throws IOException {
    stream.showText(text(value));
    stream.newLineAtOffset(0, -LEADING);
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

  private String text(String value) {
    return value.replace('\n', ' ').replace('\r', ' ');
  }
}
