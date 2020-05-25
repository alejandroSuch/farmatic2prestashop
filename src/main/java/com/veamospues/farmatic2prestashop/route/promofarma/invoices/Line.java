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
  private BigDecimal totalGrossWithoutVat;
  private Integer vat;
  private BigDecimal fee;
  private BigDecimal totalGrossWithVat;
  private BigDecimal puc;

  public Line(
    String codeOrEan, String name, Integer units, BigDecimal totalGrossWithoutVat,
    Integer vat, BigDecimal fee, BigDecimal totalGrossWithVat
  ) {
    this.code = codeOrEan.length() == 13 ? null : codeOrEan;
    this.ean = codeOrEan.length() == 13 ? codeOrEan : null;
    this.name = name;
    this.units = units;
    this.totalGrossWithoutVat = totalGrossWithoutVat;
    this.vat = vat;
    this.fee = fee;
    this.totalGrossWithVat = totalGrossWithVat;
    this.puc = null;
  }

  public void changePuc(BigDecimal puc) {
    this.puc = puc;
  }

  public void changeCode(String code) {
    this.code = code;
  }

  public boolean hasCode() {
    return this.code != null;
  }
}
