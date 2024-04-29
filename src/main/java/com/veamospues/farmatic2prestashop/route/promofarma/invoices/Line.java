package com.veamospues.farmatic2prestashop.route.promofarma.invoices;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(fluent = true)
public class Line {

  private String code;
  private String ean;
  private String name;
  private Integer units;
  private BigDecimal vat;
  private BigDecimal totalGrossWithVat;
  private BigDecimal puc;

  public Line(
    String codeOrEan,
    Integer units,
    BigDecimal vat,
    BigDecimal totalGrossWithVat
  ) {
    this.code = codeOrEan.length() == 13 ? null : codeOrEan;
    this.ean = codeOrEan.length() == 13 ? codeOrEan : null;
    this.units = units;
    this.vat = vat;
    this.totalGrossWithVat = totalGrossWithVat;
    this.puc = null;
    this.name = "";
  }

  public void changePuc(BigDecimal puc) {
    this.puc = puc;
  }

  public void changeName(String name) {
    this.name = name;
  }

  public void changeCode(String code) {
    this.code = code;
  }

  public boolean hasCode() {
    return this.code != null;
  }
}
