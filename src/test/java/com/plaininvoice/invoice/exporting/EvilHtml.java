package com.plaininvoice.invoice.exporting;

import com.plaininvoice.invoice.document.layout.*;
import com.plaininvoice.invoice.document.pagination.*;
import com.plaininvoice.invoice.document.printable.*;
import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.time.*;
import java.util.*;

final class EvilHtml {

  private EvilHtml() {}

  static PageDocument pages() {
    return new PageDocument(
      new LayoutPage("A4", "portrait", new PageMargins(1, 1, 1, 1)),
      List.of(frame())
    );
  }

  private static PageFrame frame() {
    return new PageFrame(1, header(), body(), new FooterToken("A & B <C>"));
  }

  private static HeaderToken header() {
    return new HeaderToken("A & B <C>", "INV-&-1", LocalDate.of(2026, 1, 1), "Draft");
  }

  private static PageBody body() {
    return new PageBody(parties(), lines(), totals(), terms(), true);
  }

  private static PartyBlock parties() {
    return new PartyBlock(party("seller"), party("buyer"));
  }

  private static PartyToken party(String role) {
    return new PartyToken(role, new DocumentParty("A & B <C>", "TAX", "a@example.com"));
  }

  private static LineTableToken lines() {
    return new LineTableToken(List.of(line()));
  }

  private static LineToken line() {
    return new LineToken(1, "A & B <C>", quantity(), percent(), amounts(), new BreakHint("auto", "avoid", "auto"));
  }

  private static DocumentAmounts amounts() {
    var money = money();
    return new DocumentAmounts(money, money, money, money);
  }

  private static TotalsToken totals() {
    var money = money();
    return new TotalsToken(money, money, money);
  }

  private static TermsToken terms() {
    return new TermsToken(LocalDate.of(2026, 1, 2), "A & B <C>");
  }

  private static Money money() {
    return new Money(new BigDecimal("1.00"), new CurrencyCode("USD"));
  }

  private static Quantity quantity() {
    return new Quantity(BigDecimal.ONE);
  }

  private static Percentage percent() {
    return new Percentage(BigDecimal.ZERO);
  }
}
