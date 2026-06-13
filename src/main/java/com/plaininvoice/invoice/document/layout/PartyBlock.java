package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record PartyBlock(PartyToken seller, PartyToken buyer) {
  public PartyBlock {
    Objects.requireNonNull(seller, "seller token cannot be null");
    Objects.requireNonNull(buyer, "buyer token cannot be null");
  }
}
