package com.veamospues.farmatic2prestashop.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Order implements Serializable {

  private int id;
  private String code;
  private BigDecimal total;
  private BigDecimal totalPaid;
  private BigDecimal customerDeliveryCost;
  private BigDecimal ownDeliveryCost;
  private BigDecimal boxCost;
  private BigDecimal discount;
  private LocalDateTime invoiceDate;
  private LocalDateTime deliveryDate;
  private List<Product> products;

  public Order(
    int id, String code, BigDecimal total,
    BigDecimal customerDeliveryCost, BigDecimal discount,
    LocalDateTime invoiceDate, LocalDateTime deliveryDate,
    List<Product> products,
    BigDecimal totalPaid
  ) {
    this.id = id;
    this.code = code;
    this.total = total;
    this.customerDeliveryCost = customerDeliveryCost;
    this.ownDeliveryCost = BigDecimal.valueOf(4.3);
    this.boxCost = BigDecimal.valueOf(0.47);
    this.discount = discount;
    this.invoiceDate = invoiceDate;
    this.deliveryDate = deliveryDate;
    this.products = products;
    this.totalPaid = totalPaid;
  }

  public void addProduct(Product product) {
    this.products.add(product);
  }

  public List<Product> products() {
    return Collections.unmodifiableList(products);
  }

  public BigDecimal benefit() {
    BigDecimal result = BigDecimal.ZERO;

    for (Product product : products) {
      result = result.add(product.benefit());
    }

    return result;
  }

  public BigDecimal reimbursement() {
    return this.totalPaid()
      .subtract(
        this.products.stream()
          .map(product -> product.unitPrice().multiply(BigDecimal.valueOf(product.quantity())))
          .reduce(BigDecimal.ZERO, BigDecimal::add)
          .add(this.customerDeliveryCost())
          .subtract(this.discount())
      );
  }

  public BigDecimal orderBenefit() {
    return benefit()
      .add(customerDeliveryCost())
      .subtract(ownDeliveryCost())
      .subtract(boxCost())
      .subtract(discount());
  }

  public BigDecimal orderPercentBenefit() {
    return orderBenefit().multiply(BigDecimal.valueOf(100))
      .divide(total(), 2, RoundingMode.HALF_UP);
  }

  public Product product(String code) {
    return products.stream()
      .filter(
        product -> product.code().equals(code) ||
          Integer.valueOf(product.code()).equals(Integer.valueOf(code))
      )
      .findFirst()
      .orElse(null);
  }
}
