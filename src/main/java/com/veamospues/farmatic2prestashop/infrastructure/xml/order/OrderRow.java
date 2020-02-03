package com.veamospues.farmatic2prestashop.infrastructure.xml.order;

import com.veamospues.farmatic2prestashop.dto.Product;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class OrderRow {

  String product_reference;
  String product_name;
  int product_quantity;
  double unit_price_tax_incl;
  double unit_price_tax_excl;


  @XmlElement
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  public String getProduct_reference() {
    return product_reference;
  }

  public void setProduct_reference(String product_reference) {
    this.product_reference = product_reference;
  }

  @XmlElement
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  public String getProduct_name() {
    return product_name;
  }

  public void setProduct_name(String product_name) {
    this.product_name = product_name;
  }

  @XmlElement
  public int getProduct_quantity() {
    return product_quantity;
  }

  public void setProduct_quantity(int product_quantity) {
    this.product_quantity = product_quantity;
  }

  @XmlElement
  public double getUnit_price_tax_incl() {
    return unit_price_tax_incl;
  }

  public void setUnit_price_tax_incl(double unit_price_tax_incl) {
    this.unit_price_tax_incl = unit_price_tax_incl;
  }

  @XmlElement
  public double getUnit_price_tax_excl() {
    return unit_price_tax_excl;
  }

  public void setUnit_price_tax_excl(double unit_price_tax_excl) {
    this.unit_price_tax_excl = unit_price_tax_excl;
  }

  Product toProduct() {
    return new Product(
      this.getProduct_reference(),
      this.getProduct_name(),
      this.getProduct_quantity(),
      BigDecimal.valueOf(this.getUnit_price_tax_incl()),
      BigDecimal.valueOf(this.getUnit_price_tax_excl())
    );
  }
}
