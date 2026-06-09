package com.plaininvoice.invoice.settings;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;

final class SettingsRecordTest {

  @Test
  void rejectsNullSeller() {
    assertThrows(NullPointerException.class, () -> new SettingsRecord(null, defaults()));
  }

  @Test
  void rejectsNullDefaults() {
    assertThrows(NullPointerException.class, () -> new SettingsRecord(seller(), null));
  }

  @Test
  void keepsSeller() {
    var seller = seller();
    assertThat(new SettingsRecord(seller, defaults()).seller(), is(seller));
  }

  @Test
  void keepsDefaults() {
    var defaults = defaults();
    assertThat(new SettingsRecord(seller(), defaults).defaults(), is(defaults));
  }

  private SellerProfile seller() {
    return new SellerProfile(new Party("Seller Ltd", "TAX-01", "seller@example.com"));
  }

  private InvoiceDefaults defaults() {
    return new InvoiceDefaults(
      new CurrencyCode("USD"),
      new PaymentDefault(30, "Net 30"),
      "CORE",
      List.of(new TaxPreset("VAT", new Percentage(new BigDecimal("16"))))
    );
  }
}
