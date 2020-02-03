package com.veamospues.farmatic2prestashop.dto;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Accessors(fluent = true)
public class Product implements Serializable {

  public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
  private String code;
  private String description;
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal unitPriceNoTaxes;
  private BigDecimal puc = ZERO;

  public Product(
    String code, String description, Integer quantity,
    BigDecimal unitPrice, BigDecimal unitPriceNoTaxes
  ) {
    this.code = code;
    this.description = description;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.unitPriceNoTaxes = unitPriceNoTaxes;
  }

  public BigDecimal price() {
    return unitPrice().multiply(BigDecimal.valueOf(quantity));
  }

  public void puc(BigDecimal puc) {
    this.puc = puc;
  }

  public BigDecimal taxes() {
    try {
      return unitPrice().divide(unitPriceNoTaxes(), 2, RoundingMode.HALF_UP)
        .subtract(ONE)
        .multiply(ONE_HUNDRED);
    } catch (ArithmeticException arithmethicException) {
      log.severe("Error al calcular las tasas de " + this.description());
      return BigDecimal.valueOf(-9999);
    }
  }

  public BigDecimal benefit() {
    return unitPrice().subtract(puc()).multiply(BigDecimal.valueOf(quantity()));
  }
}
