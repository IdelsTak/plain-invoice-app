package com.plaininvoice.invoice.pricing;

import java.math.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class MonetaryArithmeticPolicyTest {

  @Test
  void usesHalfUpAsDefaultRoundingMode() {
    var policy = new MonetaryArithmeticPolicy();
    assertThat(policy.roundingMode(), is(RoundingMode.HALF_UP));
  }

  @Test
  void acceptsExplicitRoundingMode() {
    var policy = new MonetaryArithmeticPolicy(RoundingMode.DOWN);
    assertThat(policy.roundingMode(), is(RoundingMode.DOWN));
  }

  @Test
  void rejectsNullRoundingMode() {
    assertThrows(NullPointerException.class, () -> new MonetaryArithmeticPolicy(null));
  }

  @Test
  void normalizesUsingConfiguredRoundingMode() {
    var policy = new MonetaryArithmeticPolicy(RoundingMode.DOWN);
    var normalized = policy.normalize(new BigDecimal("10.019"), new CurrencyCode("USD"));
    assertThat(normalized, comparesEqualTo(new BigDecimal("10.01")));
  }

  @Test
  void rejectsCurrencyWithoutDefinedMinorUnit() {
    var policy = new MonetaryArithmeticPolicy();
    assertThrows(
      IllegalArgumentException.class,
      () -> policy.normalize(new BigDecimal("10.01"), new CurrencyCode("XXX"))
    );
  }
}
