package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record FooterToken(String text) {
  public FooterToken {
    Objects.requireNonNull(text, "footer text cannot be null");
    text = text.trim();
  }
}
