package com.plaininvoice.invoice.pricing;

import java.math.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class MoneyTest {

    @Test
    void normalizesMoneyToCurrencyScale() {
        var money = new Money(new BigDecimal("12.345"), new CurrencyCode("USD"));
        assertThat(money.amount(), comparesEqualTo(new BigDecimal("12.35")));
    }

    @Test
    void rejectsMismatchedCurrencyOnAddition() {
        var usd = new Money(new BigDecimal("10.00"), new CurrencyCode("USD"));
        var eur = new Money(new BigDecimal("2.00"), new CurrencyCode("EUR"));
        assertThrows(IllegalArgumentException.class, () -> usd.add(eur));
    }

    @Test
    void multipliesMoneyByQuantityDeterministically() {
        var total = new Money(new BigDecimal("19.99"), new CurrencyCode("USD"))
          .multiply(new Quantity(new BigDecimal("3")));
        assertThat(total.amount(), comparesEqualTo(new BigDecimal("59.97")));
    }

    @Test
    void computesPercentageUsingFactor() {
        var tax = new Money(new BigDecimal("200"), new CurrencyCode("USD"))
          .percent(new Percentage(new BigDecimal("16")));
        assertThat(tax.amount(), comparesEqualTo(new BigDecimal("32.00")));
    }

    @Test
    void rejectsInvalidCurrencyCode() {
        var thrown = assertThrows(IllegalArgumentException.class, () -> new CurrencyCode("US"));
        assertThat(thrown.getMessage(), equalTo("currency code must be a 3-letter ISO code"));
    }

    @Test
    void rejectsNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(new BigDecimal("-1")));
    }

    @Test
    void rejectsPercentageAboveHundred() {
        assertThrows(IllegalArgumentException.class, () ->
          new Percentage(new BigDecimal("100.01")));
    }
}
