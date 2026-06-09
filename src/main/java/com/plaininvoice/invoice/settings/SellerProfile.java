package com.plaininvoice.invoice.settings;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public record SellerProfile(Party seller) {
  public SellerProfile {
    Objects.requireNonNull(seller, "seller profile party cannot be null");
  }
}
