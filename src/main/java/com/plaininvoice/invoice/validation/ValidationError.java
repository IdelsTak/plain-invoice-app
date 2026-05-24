package com.plaininvoice.invoice.validation;

import java.util.*;

public sealed interface ValidationError permits MissingValue, InvalidValue, RuleViolation {
  String path();

  String code();

  Map<String, String> context();
}
